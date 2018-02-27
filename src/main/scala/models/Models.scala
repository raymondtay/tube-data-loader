package tube.dataloader.models

case class Channels(ok: Boolean, channels: List[Channel], response_metadata: Option[ResponseData])

case class Users(ok: Boolean, offset: String, members: List[User], cache_ts : String, response_metadata: Option[ResponseData])

case class Team(
  id : String,
  name: String,
  domain: String,
  email_domain : String,
  image_132 : String,
  emojis : List[Emoji]
)

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
  members : List[User],
  topic : Topic,
  purpose : Purpose,
  previous_names : List[String],
  num_members : Long)

case class Topic(value: String, creator: String, last_set: Long)
case class Purpose(value: String, creator: String, last_set: Long)
case class User(
  id : String,
  team_id: String,
  name : String,
  deleted : Boolean,
  first_name : String,
  real_name : String,
  last_name : String,
  display_name : String,
  email : String,
  is_bot : Boolean,
  status_text : String,
  status_emoji : String,
  title : String,
  skype : String,
  phone : String,
  is_owner : Boolean,
  is_primary_owner : Boolean,
  image_72 : String,
  is_admin : Boolean,
  bot_id : String,
  user : String
)

