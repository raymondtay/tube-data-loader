package tube.dataloader.codec


object JsonCodec {
  import tube.dataloader.models._
  import io.circe._, io.circe.generic.semiauto._

  implicit val responseDataEncoder : Encoder[ResponseData] = deriveEncoder[ResponseData]
  implicit val userProfileEncoder : Encoder[Profile] = deriveEncoder[Profile]
  implicit val userEncoder : Encoder[User] = deriveEncoder[User]
  implicit val usersEncoder : Encoder[Users] = new Encoder[Users] {
    final def apply(a: Users) : Json = Json.obj(
      ("ok", Json.fromBoolean(a.ok)),
      ("offset", Json.fromString(a.offset)),
      ("cache_ts", Json.fromLong(a.cache_ts.toLong)),
      ("members", Json.arr(a.members.map(member ⇒ userEncoder.apply(member)) : _*)),
      ("response_metadata", if(a.response_metadata.isDefined)
        Json.obj(("next_cursor", Json.fromString(a.response_metadata.get.next_cursor))) else Json.obj(("next_cursor", Json.fromString(""))))
    )
  }
  implicit val emojiDataEncoder : Encoder[EmojiData] = deriveEncoder[EmojiData]
  implicit val emojiEncoder : Encoder[Emoji] = new Encoder[Emoji] {
    final def apply(a: Emoji) : Json = Json.obj(
      (a.name, Json.fromString(a.image))
    )
  }
  implicit val teamEncoder : Encoder[Team] = deriveEncoder[Team]
  implicit val teamDataEncoder : Encoder[TeamData] = new Encoder[TeamData] {
    final def apply(a: TeamData) : Json = Json.obj(
      ("ok", Json.fromBoolean(true)),
      ("team", teamEncoder.apply(a.team))
    )
  }
  implicit val iconEncoder : Encoder[Icon] = deriveEncoder[Icon]
  implicit val topicEncoder : Encoder[Topic] = deriveEncoder[Topic]
  implicit val purposeEncoder : Encoder[Purpose] = deriveEncoder[Purpose]
  implicit val channelEncoder : Encoder[Channel] = deriveEncoder[Channel]
  implicit val channelsEncoder : Encoder[Channels] = new Encoder[Channels] {
    final def apply(a: Channels) : Json = Json.obj(
      ("ok", Json.fromBoolean(a.ok)),
      ("channels", Json.arr(a.channels.map(channel ⇒ channelEncoder.apply(channel)) : _*)),
      ("response_metadata", if(a.response_metadata.isDefined)
        Json.obj(("next_cursor", Json.fromString(a.response_metadata.get.next_cursor))) else Json.obj(("next_cursor", Json.fromString(""))))
    )
  }
  implicit val reactionEncoder : Encoder[Reaction] = deriveEncoder[Reaction]
  implicit val botAttEncoder : Encoder[BotAttachment] = deriveEncoder[BotAttachment]
  implicit val replyEncoder : Encoder[Reply] = deriveEncoder[Reply]
  implicit val fileCommentEncoder : Encoder[UserFileComment] = deriveEncoder[UserFileComment]
  implicit val userFileShareEncoder : Encoder[UserFileShareMessage] = deriveEncoder[UserFileShareMessage]
  implicit val userAttMsgEncoder : Encoder[UserAttachmentMessage] = deriveEncoder[UserAttachmentMessage]
  implicit val botAttMsgEncoder : Encoder[BotAttachmentMessage] = deriveEncoder[BotAttachmentMessage]
  implicit val userFileShareCommentEncoder : Encoder[UserFileShareCommentMessage] = deriveEncoder[UserFileShareCommentMessage]
  implicit val botPlainMsgEncoder : Encoder[PlainBotMessage] = deriveEncoder[PlainBotMessage]
  implicit val messageEncoder : Encoder[MessageTypes] = new Encoder[MessageTypes] {
    final def apply(a: MessageTypes) : Json = {
      import tube.dataloader.models.extract
      (a map extract).unify
    }
  }

  implicit val channelPostsEncoder : Encoder[Messages] = new Encoder[Messages] {
    final def apply(a: Messages) : Json = Json.obj(
      ("ok", Json.fromBoolean(a.ok)),
      ("messages", Json.arr(a.messages.map(msg ⇒ messageEncoder.apply(msg)) : _*)),
      ("response_metadata", if(a.response_metadata.isDefined)
        Json.obj(("next_cursor", Json.fromString(a.response_metadata.get.next_cursor))) else Json.obj(("next_cursor", Json.fromString(""))))
    )
  }

}

