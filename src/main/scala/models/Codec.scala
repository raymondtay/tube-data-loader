package tube.dataloader.codec


object JsonCodec {
  import tube.dataloader.models._
  import io.circe._, io.circe.generic.semiauto._

  implicit val responseDataEncoder : Encoder[ResponseData] = deriveEncoder[ResponseData]
  implicit val userEncoder : Encoder[User] = deriveEncoder[User]
  implicit val usersEncoder : Encoder[Users] = new Encoder[Users] {
    final def apply(a: Users) : Json = Json.obj(
      ("ok", Json.fromBoolean(a.ok)),
      ("offset", Json.fromString(a.offset)),
      ("members", Json.arr(a.members.map(member ⇒ userEncoder.apply(member)) : _*)),
      ("response_metadata", if(a.response_metadata.isDefined) Json.fromString(a.response_metadata.get.next_cursor) else Json.fromString(""))
    )
  }
  implicit val emojiEncoder : Encoder[Emoji] = deriveEncoder[Emoji]
  implicit val teamEncoder : Encoder[Team] = deriveEncoder[Team]
  implicit val topicEncoder : Encoder[Topic] = deriveEncoder[Topic]
  implicit val purposeEncoder : Encoder[Purpose] = deriveEncoder[Purpose]
  implicit val channelEncoder : Encoder[Channel] = deriveEncoder[Channel]
  implicit val channelsEncoder : Encoder[Channels] = new Encoder[Channels] {
    final def apply(a: Channels) : Json = Json.obj(
      ("ok", Json.fromBoolean(a.ok)),
      ("channels", Json.arr(a.channels.map(channel ⇒ channelEncoder.apply(channel)) : _*)),
      ("response_metadata", if(a.response_metadata.isDefined) Json.fromString(a.response_metadata.get.next_cursor) else Json.fromString(""))
    )
  }
}

