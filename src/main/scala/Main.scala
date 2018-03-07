package tube.dataloader.core

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

import tube.dataloader.core.generator.Generators
import tube.dataloader.codec.JsonCodec
import tube.dataloader.models._

/**
  * Given the following parameters, we generate the data sets for the following
  */
object WebServer extends App {

  import Generators.{genUsers, genTeam, genTeamEmojis, genChannels, genAllMessagesForChannelForTimeRange}
  import JsonCodec._
  import java.time._, temporal._
  import io.circe._, syntax._

  implicit val system = ActorSystem("tube-data-loader")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher
  val team : Team = genTeam.sample.get
  val teamEmojis : EmojiData = genTeamEmojis.sample.get
  val users : List[User] = genUsers(team).sample.get
  val channels : List[Channel] = genChannels(users).sample.get
  val source = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 0)
  val target = LocalDateTime.of(2018, Month.JANUARY, 25, 0,0)
  val messages =
    genAllMessagesForChannelForTimeRange(team)(users)(10)(10)(10)(10)(30, ChronoUnit.MINUTES, source, target).sample.get

  val channelSession : Int ⇒ Array[Channels] = Session.apply(channels) _
  val usersSession : Int ⇒ Array[Users] = Session.apply(users) _
  val channelPostsSession : Int ⇒ Array[Messages] = Session.apply(messages) _

  var usersState : Array[Users] = _
  var channelsState : Array[Channels] = _
  var channelPostsState : Array[Messages] = _

  /* 
   * We don't perform validation on the token itself considering that this tool
   * is largely used for internal performance evaluation. EVerything here
   * follows Slack's API specification - we're just mocking the behavior.
   *
   * If something went wrong, DO check the slack's API documentation
   */
  val route =
    path("users") {
      get {
        parameters('limit.as[Int]?, 'cursor?, 'token) {
          (limit, cursor, token) ⇒
            println(s"[users] token ⇒ ${token}, limit ⇒ ${limit}, cursor ⇒ ${cursor}")
            var usersToBeReturned : List[Users] = Nil

            if(cursor.isEmpty) { /* new request with no limit defined, meaning it wants everything */
              if (limit.isDefined) {
                usersState = usersSession(limit.get)
                usersToBeReturned = usersState.head :: Nil
              } else {
                usersState = usersSession(users.size)
                usersToBeReturned = usersState.toList
              }
            }

            if(!cursor.isEmpty) { /* existing request and looking for a specific cursor state */
              if (usersState == null) usersState = usersSession(users.size)
              usersToBeReturned =
                usersState.filter(t ⇒ if (t.response_metadata.isDefined) t.response_metadata.get.next_cursor == cursor.get.toInt + 1 + ""  else false).toList match {
                  case Nil ⇒ usersState.last :: Nil
                  case xs  ⇒ xs
                }
            }

            complete(HttpEntity(ContentTypes.`application/json`, usersToBeReturned.head.asJson.noSpaces))
        }
      }
    } ~ 
    path("team") {
      get {
        parameters('limit.as[Int]?, 'cursor?, 'token) {
          (limit, cursor, token) ⇒
            println(s"[team] token ⇒ ${token}, limit ⇒ ${limit}, cursor ⇒ ${cursor}")
            complete(HttpEntity(ContentTypes.`application/json`, TeamData(true, team).asJson.noSpaces))
        }
      }
    } ~
    path("team.emoji") {
      get {
        parameters('limit.as[Int]?, 'cursor?, 'token) {
          (limit, cursor, token) ⇒
            println(s"[team-emoji] token ⇒ ${token}, limit ⇒ ${limit}, cursor ⇒ ${cursor}")
            complete(HttpEntity(ContentTypes.`application/json`, teamEmojis.asJson.noSpaces))
        }
      }
    } ~
    path("channels") {
      get {
        parameters('limit.as[Int]?, 'cursor?, 'token) {
          (limit, cursor, token) ⇒
            println(s"[channels] token ⇒ ${token}, limit ⇒ ${limit}, cursor ⇒ ${cursor}")
            var channelsToBeReturned : List[Channels] = Nil

            if(cursor.isEmpty) { /* new request with no limit defined, meaning it wants everything */
              if (limit.isDefined) {
                channelsState = channelSession(limit.get)
                channelsToBeReturned = channelsState.head :: Nil
              } else {
                channelsState = channelSession(channels.size)
                channelsToBeReturned = channelsState.toList
              }
            }

            if(!cursor.isEmpty) { /* existing request and looking for a specific cursor state */
              if (channelsState == null) channelsState = channelSession(channels.size)
              channelsToBeReturned =
                channelsState.filter(t ⇒ if(t.response_metadata.isDefined) t.response_metadata.get.next_cursor == cursor.get.toInt + 1 + "" else false).toList match {
                  case Nil ⇒ channelsState.last :: Nil
                  case xs  ⇒ xs
                }
            }
 
            complete(HttpEntity(ContentTypes.`application/json`, channelsToBeReturned.head.asJson.noSpaces))
        }
      }
    } ~ 
    path("channelposts") {
      get {
        parameters('limit.as[Int]?, 'cursor?, 'token) {
          (limit, cursor, token) ⇒
            println(s"[channelposts] token ⇒ ${token}, limit ⇒ ${limit}, cursor ⇒ ${cursor}")
            var channelPostsToBeReturned : List[Messages] = Nil

            if(cursor.isEmpty) { /* new request with no limit defined, meaning it wants everything */
              if (limit.isDefined) {
                channelPostsState = channelPostsSession(limit.get)
                channelPostsToBeReturned = channelPostsState.head :: Nil
              } else {
                channelPostsState = channelPostsSession(users.size)
                channelPostsToBeReturned = channelPostsState.toList
              }
            }

            if(!cursor.isEmpty) { /* existing request and looking for a specific cursor state */
              if (channelPostsState == null) channelPostsState = channelPostsSession(messages.size)
              channelPostsToBeReturned =
                channelPostsState.filter(t ⇒ if (t.response_metadata.isDefined) t.response_metadata.get.next_cursor == cursor.get.toInt + 1 + ""  else false).toList match {
                  case Nil ⇒ channelPostsState.last :: Nil
                  case xs  ⇒ xs
                }
            }

            complete(HttpEntity(ContentTypes.`application/json`, channelPostsToBeReturned.head.asJson.noSpaces))
        }
      }
    } 
  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  println(s"Server online at http://0.0.0.0:8080/...")

}
