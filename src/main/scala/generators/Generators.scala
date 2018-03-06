package tube.dataloader.core.generator

import scala.language.postfixOps
import org.scalacheck._
import Arbitrary.arbitrary
import Gen._

// Data Generators are often tools that developers need to do but there are
// seldom good data generators. ScalaCheck evolved from QuickCheck (which is
// the defactor data generator for Haskell) and has been maintained
// continuously by Scala developers over the last few years.
//

trait Limits {
  implicit val upperBoundUsers : Int
  implicit val upperBoundChannels : Int
  implicit val upperBoundEmojis : Int
}

object Generators extends Limits {
  import java.time._, temporal._
  import tube.dataloader.models._
  import cats.implicits._

  val config = com.typesafe.config.ConfigFactory.load()

  // No validation at all
  implicit val upperBoundUsers : Int = config.getInt("generate.users")
  implicit val upperBoundChannels : Int = config.getInt("generate.channels")
  implicit val upperBoundEmojis : Int = config.getInt("generate.team_emojis")

  // Upper bound of messages to generate per timestamp.
  private[this] val maxBotMessagesPerTS = 10
  private[this] val maxUserAttachMessagesPerTS = 10
  private[this] val maxFileShareMessagesPerTS = 10


  private[this] val firstnames = scala.io.Source.fromResource("firstnames.data").getLines.toList
  private[this] val lastnames  = scala.io.Source.fromResource("lastnames.data").getLines.toList
  private[this] val slackMessageTypes = Set("file_share", "bot_message", "file_comment")

  def genBoolean : Gen[Boolean] = oneOf[Boolean](Seq(true, false))

  def genCreatedTS : Gen[Long] = arbitrary[Long]

  def genTeamId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"T${a}${b}MQT"

  def genUserId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"U${a}${b}MQT"

  def genBotUserId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"BOT${a}${b}MQT"

  def genChannelId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"C${a}${b}MQT"

  def genChannelName : Gen[String] = for {
    a ← alphaNumStr
  } yield s"channel-$a"

  def genTopic : Gen[Topic] = for {
    v ← alphaNumStr
    lastSet ← arbitrary[Long]
  } yield Topic(v, "SlackAdmin", lastSet)

  def genPurpose : Gen[Purpose] = for {
    v ← alphaNumStr
    lastSet ← arbitrary[Long]
  } yield Purpose(v, "SlackAdmin", lastSet)

  def genFirstName : Gen[String] = oneOf(firstnames)

  def genLastName : Gen[String] = oneOf(lastnames)

  def genBotUser(team: Team) : Gen[User] = for {
    id ← genBotUserId
    teamId ← oneOf(team.id ::Nil)
    firstName ← genFirstName
    lastName ← genLastName
    deleted ← genBoolean
    isBot ← genBoolean
    isOwner ← genBoolean
    isPrimaryOwner ← genBoolean
    isAdmin ← genBoolean
    profile ← genUserProfile(firstName, lastName)
  } yield User(id, teamId, bot_id = id, user = id, name = s"${firstName}_${lastName}".toLowerCase, deleted, isBot, isOwner, isPrimaryOwner, isAdmin, profile)

  def genUserProfile(firstName: String, lastName: String) = for {
    email ← genEmail
    status ← genStatus
    emoji ← genEmoji
    title ← genTitle
    skype ← genSkypeId
    phone ← genPhoneNum
  } yield Profile(firstName, firstName + lastName, lastName, s"$firstName $lastName", email, status, emoji, title, skype, "+65"+phone, "https://a.slack-edge.com/0180/img/slackbot_72.png")

  def genUser(team: Team) : Gen[User] = for {
    id ← genUserId
    teamId ← oneOf(team.id ::Nil)
    deleted ← genBoolean
    firstName ← genFirstName
    lastName ← genLastName
    email ← genEmail
    isBot ← genBoolean
    isOwner ← genBoolean
    isPrimaryOwner ← genBoolean
    isAdmin ← genBoolean
    profile ← genUserProfile(firstName, lastName)
  } yield User(id, teamId, bot_id = "", user = id, name = s"${firstName}_${lastName}".toLowerCase, deleted, isBot, isOwner, isPrimaryOwner, isAdmin, profile)

  def genPhoneNum : Gen[Int] = arbitrary[Int].retryUntil(n ⇒ n > 0 && n.toString.length == 8) // length of phone numbers should be 8, for now.

  def genSkypeId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(n ⇒ n > 0 && n.toString.length == 8)
  } yield s"Skypeid$a"

  def genEmail: Gen[String]  = for {
    prefix ← alphaStr.retryUntil(e ⇒ !e.isEmpty && e.length < 10)
    email_domain ← oneOf(Seq("@nugit.co", "@abc.com", "@acm.org", "@acme.org"))
  } yield s"$prefix$email_domain"

  def genStatus : Gen[String] = oneOf(Seq("online", "offline"))

  def genEmoji : Gen[String] = oneOf(Seq(":)", ";)"))

  def genTeamEmojis : Gen[EmojiData] = for {
    emoji ← genEmoji2
  } yield EmojiData(true, emoji)

  def genTitle : Gen[String] = oneOf(Seq("boss", "worker"))

  // hard limit of limiting each channel to have a maximum of `upperBoundUsers` users i.e.
  // [0,`upperBoundUsers`)
  def genChannel(users : List[User]): Gen[Channel]  = for {
    id ← genChannelId
    name ← genChannelName
    isChannel ← genBoolean
    created ← genCreatedTS
    isArchived ← genBoolean
    isGeneral ← genBoolean
    isShared ← genBoolean
    isOrgShared ← genBoolean
    isMember ← genBoolean
    isPrivate ← genBoolean
    isMpim ← genBoolean
    members ← pick(upperBoundUsers, users) // each channel can have a maximum of `upperBoundUsers` users
    topic ← genTopic
    purpose ← genPurpose
  } yield Channel(id, name, isChannel, created, "SlackAdmin", isArchived, isGeneral, name, isShared, isOrgShared, isMember, isPrivate, isMpim, members.map(_.id).to[List], topic, purpose, Nil, members.size )

  // The JVM ecosystem can deal with the unicode characters but when
  // transported over to Cerebro (which is written in Python), additional
  // supported is needed to deal with unicode. Commenting out the
  // functionality.
  // val unicodeChar: Gen[Char] = Gen.oneOf((Char.MinValue to Char.MaxValue).filter(Character.isDefined(_)))

  def genEmoji2 = for {
    name ← alphaStr
  } yield Emoji(name, "https://my.slack.com/emoji/bowtie/46ec6f2bb0.png")

  def genTeam = for {
    id ← genTeamId
    name ← alphaStr
    domain ← alphaStr
    email_domain ← oneOf(Seq("@nugit.co", "@abc.com", "@acm.org", "@acme.org"))
    enterprise_id ← alphaStr
    enterprise_name ← alphaStr
    icon ← genIcon
  } yield Team(id, name, domain, email_domain, enterprise_id, enterprise_name, icon)

  def genIcon = for {
    img34 ← alphaStr
    img44 ← alphaStr
    defaultImage ← genBoolean
  } yield Icon(img34, img44, defaultImage)

  // Generates a list of Bots and "human" users with 50:50 chance
  def genUsers(team: Team) : Gen[List[User]] = {
    val genInternal =
     frequency(
       (5, genUser(team)),
       (5, genBotUser(team))
     )
    listOfN(upperBoundUsers, genInternal)
  }

  def genChannels(users: List[User]) : Gen[List[Channel]] = {
    listOfN(upperBoundChannels, genChannel(users))
  }

  def genBotAttachment = for {
    fallback ← alphaNumStr
    text ← alphaNumStr
    pretext ← alphaNumStr
    id ← posNum[Long]
    color ← oneOf(List("7575"))
    mrkdwn_in ← listOfN(choose(1, 5).sample.get, alphaNumStr)
  } yield BotAttachment(fallback, text, pretext, id, color, mrkdwn_in)

  // Foreach reaction, we generate between [1,10] users that would react to it.
  def genReaction(user : Gen[User]) = {
    val _users : List[User] = listOfN(choose(1, 10).sample.get, user.sample.get).sample.get
    for {
      name ← alphaNumStr
      users ← pick(_users.size, _users)
    } yield Reaction(name, _users.map(_.id), users.size)
  }

  // For a given timestamp, generate 1 file comment
  def genFileComment(users: List[User])(ts: LocalDateTime) = for {
    user ← oneOf(users)
    id ← alphaNumStr
  } yield UserFileComment(id, ts.toEpochSecond(ZoneOffset.UTC), user.id)

  def genFileCommentMessage(user: User)(ts : LocalDateTime) = for {
    text ← alphaStr
    fileComment ← listOfN(1, genFileComment(user::Nil)(ts))
    is_intro ← genBoolean
  } yield UserFileShareCommentMessage("message", "file_comment", text, file = RawJson.fileshared, fileComment.head, is_intro, ts.toEpochSecond(ZoneOffset.UTC).toString) map pure

  // For a given timestamp, generate 1 reply
  def genReply(users: List[User], ts: LocalDateTime) = for {
    user ← oneOf(users)
  } yield Reply(ts.toEpochSecond(ZoneOffset.UTC).toString, user.id)

  // Bot messages with no attachments, no reactions, no replies, just bot
  // messages e.g. 
  // {
  //   "text": "Hello. Another new user going by the name of Priscilla (<mailto:priscilla@nugit.co|priscilla@nugit.co>) has passed through the Nugit gates. We've also added them to Mailchimp's Nugit Users list.  ",
  //   "username": "Zapier",
  //   "bot_id": "B0VD275DX",
  //   "type": "message",
  //   "subtype": "bot_message",
  //   "ts": "1520233018.000176"
  //   }
  def genPlainBotMessage(team: Team)
                   (botUser : User)
                   (ts: LocalDateTime) =
  for {
    text ← alphaStr
  } yield PlainBotMessage("message", subtype = "bot_message", botUser.name, text, ts.toEpochSecond(ZoneOffset.UTC).toString) map pure
 
  // For a given timestamp, this function generates a message depending on
  // whether its a bot; which carries not only an attachment but also reactions
  // and replies to that attachment
  def genBotMessage(team: Team)
                   (users: List[User])
                   (botUser : User)
                   (attachmentThreshold: Int) // maximum number of attachments to generate per bot message
                   (reactionThreshold: Int) // maximum number of reactions to generate
                   (repliesThreshold: Int) // maximum number of replies to generate
                   (ts : LocalDateTime) =
    for {
      text ← alphaStr
      attachments ← listOfN(choose(1, attachmentThreshold).sample.get, genBotAttachment)
      reactions ← listOfN(choose(1, reactionThreshold).sample.get, genReaction(genUser(team)))
      replies ← listOfN(choose(1, repliesThreshold).sample.get, genReply(users, ts))
    } yield BotAttachmentMessage("message", "bot_message", user = "", botUser.id, text, attachments, ts.toEpochSecond(ZoneOffset.UTC).toString, reactions, replies) map pure

  // For a given timestamp, this function generates a message for a non-bot
  // user; which carries 1 attachment and reactions and replies to that
  // attachment
  def genUserAttachmentMessage(team: Team)
                              (users: List[User])
                              (attachmentThreshold: Int) // maximum number of attachments to generate per bot message
                              (reactionThreshold: Int) // maximum number of reactions to generate
                              (repliesThreshold: Int) // maximum number of replies to generate
                              (ts : LocalDateTime) = for {
      user ← oneOf(users)
      text ← alphaStr
      attachments ← listOfN(1, RawJson.userAttachment)
      reactions ← listOfN(choose(1, reactionThreshold).sample.get, genReaction(genUser(team)))
      replies ← listOfN(choose(1, repliesThreshold).sample.get, genReply(users, ts))
    } yield UserAttachmentMessage("message", user.id , text, attachments, ts.toEpochSecond(ZoneOffset.UTC).toString, reactions, replies) map pure

  // For a given timestamp, this function generates a file share message for a bot or non-bot
  // user; which carries 1 file shared and comments to that attachment
  def genFileShareMessage(team: Team)
                         (users: List[User])
                         (commentsThreshold: Int) // maximum number of comments to generate per file share message
                         (ts : LocalDateTime) = for {
      user ← oneOf(users)
      text ← alphaStr
  } yield UserFileShareMessage("message", "file_share", text, RawJson.fileshared, user.id, user.name, if(user.is_bot) user.bot_id else "", false, ts.toEpochSecond(ZoneOffset.UTC).toString) map pure


  /**
    * This is a higher-order combinator where we leverage the different data
    * generators to generate the data we need. A recurring pattern we see all
    * the time; we basically take in a pre-generated container of users (which
    * will obey the statistical biases we put in place. see [[genUsers]])
    *
    * Next, we generate bot messages using the synthetic bot users together
    * with generating user attachment messages and file shares by users.
    *
    * Each timestamp will generate at most 30 messages of each kind as we
    * didn't want the memory pressure problem.
    *
    * Caveats:
    * + Bots and users cannot comment on an earlier time - its something we might
    *   remove if it improves a particular aspect of the load testing.
    */
  def genAllMessagesForChannel(team: Team)
                              (users: List[User])
                              (attachmentThreshold: Int) // maximum number of attachments to generate per bot message
                              (reactionThreshold: Int) // maximum number of reactions to generate
                              (repliesThreshold: Int) // maximum number of replies to generate
                              (commentsThreshold: Int) // maximum number of comments to generate per file share message
                              (ts : LocalDateTime) : Gen[List[Gen[MessageTypes]]] = {
    import io.circe._, syntax._
    import tube.dataloader.codec.JsonCodec._

    val botUsers = users.filter(_.is_bot == true)
    val nonBotUsers = users.filter(_.is_bot == false)

    for {
      genbotUsers            ← pick(choose(1, botUsers.size).sample.get, botUsers)
      plainbotMessages       ← listOfN(maxBotMessagesPerTS, genPlainBotMessage(team)(genbotUsers.sample.get.head)(ts))
      botMessages            ← listOfN(maxBotMessagesPerTS, genBotMessage(team)(users)(genbotUsers.sample.get.head)(attachmentThreshold)(reactionThreshold)(repliesThreshold)(ts))
      userAttachmentMessages ← listOfN(maxUserAttachMessagesPerTS, genUserAttachmentMessage(team)(nonBotUsers)(attachmentThreshold)(reactionThreshold)(repliesThreshold)(ts))
      fileShareMessages      ← listOfN(maxFileShareMessagesPerTS, genFileShareMessage(team)(nonBotUsers)(commentsThreshold)(ts))
      fileShareComments      ← listOfN(maxFileShareMessagesPerTS, genFileCommentMessage(nonBotUsers.sample.get.head)(ts))
    } yield {
      botMessages ++ userAttachmentMessages ++ fileShareMessages ++ fileShareComments
    }
  }

  def genAllMessagesForChannelForTimeRange(team: Team)
                                          (users: List[User])
                                          (attachmentThreshold: Int) // maximum number of attachments to generate per bot message
                                          (reactionThreshold: Int) // maximum number of reactions to generate
                                          (repliesThreshold: Int) // maximum number of replies to generate
                                          (commentsThreshold: Int) // maximum number of comments to generate per file share message
                                          (timeAmt : Int, timeUnit: ChronoUnit, source : LocalDateTime, target: LocalDateTime) : cats.data.NonEmptyList[List[Gen[MessageTypes]]] = {
    import DateTimeGenerators.generateDateTimeRange

    for {
      ts ← generateDateTimeRange(timeAmt)(timeUnit)(source, target).value
    } yield genAllMessagesForChannel(team)(users)(attachmentThreshold)(reactionThreshold)(repliesThreshold)(commentsThreshold)(ts).sample.get.toList
  }

}

 /**
  * Here's how to use the date time range generator which generates all time
  * ranges with a 30 minute interval.
  *
  * scala> val source = LocalDateTime.of(2018, Month.JANUARY, 1, 0, 0)
  * scala> val target = LocalDateTime.of(2018, Month.FEBRUARY, 1, 0,0)
  * scala> generateDateTimeRange(30)(ChronoUnit.MINUTES)(source, target).value
  */
object DateTimeGenerators {
  import cats._, free._, data._, implicits._
  import java.time._, temporal._

  private val secondsInDay = 86400L

  // Starting from a pre-defined start date time, we allow developers the
  // ability to generate a time-range with the interval given by the
  // "chronoUnit" (e.g. hours, days, minutes etc) and the given "amount"
  def generateDateTimeEvery(amount: Long)(chronoUnit : ChronoUnit)(source: LocalDateTime, target: LocalDateTime) : Cofree[Option, LocalDateTime] =
    Cofree.unfold[Option, LocalDateTime](source){
      (dt: LocalDateTime) ⇒
        val duration = Duration.between(dt, target).getSeconds
        if (duration < secondsInDay) none // terminating condition
        else { dt.plus(amount, chronoUnit).some }
    }

  // Generating date times on a 20 minute interval does not seem to throw SOE
  // for 1 month; we might need a divide-conquer approach to generating more
  // closer ranged times.
  def generateDateTimeRange(interval : Int)(timeUnit : ChronoUnit)(source: LocalDateTime, target: LocalDateTime) =
    Cofree.cata[Option, LocalDateTime, NonEmptyList[LocalDateTime]](generateDateTimeEvery(interval)(timeUnit)(source, target))(
      (i, il) ⇒ Eval.now(NonEmptyList(i, il.fold[List[LocalDateTime]](Nil)(_.toList)))
    )

}

