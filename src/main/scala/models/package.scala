package tube.dataloader

package object models {

  import shapeless._

  type MessageTypes = UserFileShareMessage :+: UserAttachmentMessage :+: PlainBotMessage :+: BotAttachmentMessage :+: UserFileShareCommentMessage :+: CNil

  object pure extends Poly1 {
    import io.circe.syntax._
    import tube.dataloader.codec.JsonCodec._
    implicit def whenUserFile = at[UserFileShareMessage](i ⇒ Coproduct[MessageTypes](i))
    implicit def whenUserFileComment = at[UserFileShareCommentMessage](i ⇒ Coproduct[MessageTypes](i))
    implicit def whenUserAttach = at[UserAttachmentMessage](i ⇒ Coproduct[MessageTypes](i))
    implicit def whenBotAttach = at[BotAttachmentMessage](i ⇒ Coproduct[MessageTypes](i))
    implicit def whenPlainBotMsg = at[PlainBotMessage](i ⇒ Coproduct[MessageTypes](i))
  }

  object extract extends Poly1 {
    import io.circe.syntax._
    import tube.dataloader.codec.JsonCodec._
    implicit def whenUserFile = at[UserFileShareMessage](i ⇒ i.asJson)
    implicit def whenUserFileComment = at[UserFileShareCommentMessage](i ⇒ i.asJson)
    implicit def whenUserAttach = at[UserAttachmentMessage](i ⇒ i.asJson)
    implicit def whenBotAttach = at[BotAttachmentMessage](i ⇒ i.asJson)
    implicit def whenPlainBotMsg = at[PlainBotMessage](i ⇒ i.asJson)
  }
}
