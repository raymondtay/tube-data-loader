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
  val config = com.typesafe.config.ConfigFactory.load()

  // No validation at all
  implicit val upperBoundUsers = config.getInt("generate.users")
  implicit val upperBoundChannels = config.getInt("generate.channels")
  implicit val upperBoundEmojis = config.getInt("generate.team_emojis")

  import tube.dataloader.models._

  private[this] val firstnames = scala.io.Source.fromResource("firstnames.data").getLines.toList
  private[this] val lastnames  = scala.io.Source.fromResource("lastnames.data").getLines.toList

  def genBoolean : Gen[Boolean] = oneOf[Boolean](Seq(true, false))

  def genCreatedTS : Gen[Long] = arbitrary[Long]

  def genTeamId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Char].retryUntil(Character.isDefined(_))
    c ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"T${a}${b}${c}MQT"

  def genUserId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Char].retryUntil(Character.isDefined(_))
    c ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"U${a}${b}${c}MQT"

  def genBotUserId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Char].retryUntil(Character.isDefined(_))
    c ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"BOT${a}${b}${c}MQT"

  def genChannelId : Gen[String] = for {
    a ← arbitrary[Int].retryUntil(_ > 0)
    b ← arbitrary[Char].retryUntil(Character.isDefined(_))
    c ← arbitrary[Int].retryUntil(_ > 0)
  } yield s"C${a}${b}${c}MQT"

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
    deleted ← genBoolean
    firstName ← genFirstName
    lastName ← genLastName
    email ← genEmail
    isBot ← genBoolean
    status ← genStatus
    emoji ← genEmoji
    title ← genTitle
    skype ← genSkypeId
    phone ← genPhoneNum
    isOwner ← genBoolean
    isPrimaryOwner ← genBoolean
    isAdmin ← genBoolean
  } yield User(id, teamId, s"${firstName}_${lastName}".toLowerCase, deleted, firstName, firstName+ lastName, lastName, s"$firstName $lastName", email, isBot, status, emoji, title, skype, "+65"+phone, isOwner, isPrimaryOwner, image_72 = "https://a.slack-edge.com/0180/img/slackbot_72.png", isAdmin, bot_id = id, user = id)

  def genUser(team: Team) : Gen[User] = for {
    id ← genUserId
    teamId ← oneOf(team.id ::Nil)
    deleted ← genBoolean
    firstName ← genFirstName
    lastName ← genLastName
    email ← genEmail
    isBot ← genBoolean
    status ← genStatus
    emoji ← genEmoji
    title ← genTitle
    skype ← genSkypeId
    phone ← genPhoneNum
    isOwner ← genBoolean
    isPrimaryOwner ← genBoolean
    isAdmin ← genBoolean
  } yield User(id, teamId, s"${firstName}_${lastName}".toLowerCase, deleted, firstName, firstName+ lastName, lastName, s"$firstName $lastName", email, isBot, status, emoji, title, skype, "+65"+phone, isOwner, isPrimaryOwner, image_72 = "https://a.slack-edge.com/0180/img/slackbot_72.png", isAdmin, bot_id = "", user = id)

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
  } yield Channel(id, name, isChannel, created, "SlackAdmin", isArchived, isGeneral, name, isShared, isOrgShared, isMember, isPrivate, isMpim, members.to[List], topic, purpose, Nil, members.size )

  val unicodeChar: Gen[Char] = Gen.oneOf((Char.MinValue to Char.MaxValue).filter(Character.isDefined(_)))

  def genEmoji2 = for {
    name ← alphaStr
    image ← unicodeChar
  } yield Emoji(name, image+"")

  def genTeam = for {
    id ← genTeamId
    name ← alphaStr
    domain ← alphaStr
    email_domain ← oneOf(Seq("@nugit.co", "@abc.com", "@acm.org", "@acme.org"))
    emojis ← listOfN(upperBoundEmojis, genEmoji2)
  } yield Team(id, name, domain, email_domain, image_132 = "https://a.slack-edge.com/0180/img/slackbot_72.png", emojis)

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

}
