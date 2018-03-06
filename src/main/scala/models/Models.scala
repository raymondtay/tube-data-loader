package tube.dataloader.models

case class Channels(ok: Boolean, channels: List[Channel], response_metadata: Option[ResponseData])

case class Messages(ok: Boolean, messages: List[MessageTypes], response_metadata: Option[ResponseData])

case class Users(ok: Boolean, offset: String, members: List[User], cache_ts : String, response_metadata: Option[ResponseData])

case class TeamData(ok: Boolean, team : Team) 

case class Team(
  id : String,
  name: String,
  domain: String,
  email_domain : String,
  enterprise_id : String, 
  enterprise_name : String,
  icon : Icon
)

case class Icon( image_34 : String, image_44 : String, image_default : Boolean, image_132 : String = "https://a.slack-edge.com/0180/img/slackbot_72.png")

case class EmojiData(ok : Boolean, emoji : Emoji)

case class Emoji(name : String, image: String)

case class ResponseData(next_cursor: String)
case class Channel(id : String,
  name : String,
  is_channel: Boolean,
  created: Long,
  creator: String,
  is_archived: Boolean,
  is_general: Boolean,
  name_normalized: String,
  is_shared: Boolean,
  is_org_shared : Boolean,
  is_member : Boolean,
  is_private : Boolean,
  is_mpim : Boolean,
  members : List[String],
  topic : Topic,
  purpose : Purpose,
  previous_names : List[String],
  num_members : Long)

case class Topic(value: String, creator: String, last_set: Long)
case class Purpose(value: String, creator: String, last_set: Long)
case class User(
  id : String,
  team_id: String,
  bot_id : String,
  user : String,
  name : String,
  deleted : Boolean,
  is_bot : Boolean,
  is_owner : Boolean,
  is_primary_owner : Boolean,
  is_admin : Boolean,
  profile : Profile
)

case class Profile(
  first_name : String,
  real_name : String,
  last_name : String,
  display_name : String,
  email : String,
  status_text : String,
  status_emoji : String,
  title : String,
  skype : String,
  phone : String,
  image_72 : String
)

case class BotAttachment(
  fallback : String,
  text : String,
  pretext : String,
  id : Long,
  color : String,
  mrkdwn_in : List[String]
)

case class PlainBotMessage(
  `type`: String,
  subtype : String,
  username : String,
  text: String,
  ts : String
)

case class BotAttachmentMessage(
  `type`: String,
  subtype : String,
  user: String = "",
  bot_id: String,
  text: String,
  attachments: List[BotAttachment],
  ts: String,
  reactions: List[Reaction],
  replies: List[Reply])

case class UserAttachmentMessage(
  `type`: String,
  user: String,
  text: String,
  attachments: List[io.circe.Json],
  ts: String,
  reactions: List[Reaction],
  replies: List[Reply])

case class Reaction(
  name : String,
  users: List[String],
  count : Int
)

case class Reply(ts: String, user: String)

case class UserFileShareMessage(
  `type`: String,
  subtype: String,
  text : String,
  file : io.circe.Json,
  user : String,
  username: String,
  bot_id : String,
  display_as_bot : Boolean,
  ts: String
)

case class UserFileComment(
  id : String,
  timestamp : Long,
  user : String
)

case class UserFileShareCommentMessage(
  `type` : String,
  subtype : String,
  text : String,
  file : io.circe.Json,
  comment : UserFileComment,
  is_intro : Boolean,
  ts : String
)

