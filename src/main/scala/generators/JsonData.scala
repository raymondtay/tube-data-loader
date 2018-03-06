package tube.dataloader.core.generator

object RawJson {
  import cats.implicits._
  import io.circe._, parser._, syntax._

  val userAttachment : io.circe.Json = parse("""
  {
     "id" : 1,
     "title_link" : "https://www.fastcodesign.com/90153387/inside-pinterests-12-person-ai-team-that-is-taking-on-google",
     "from_url" : "https://www.fastcodesign.com/90153387/inside-pinterests-12-person-ai-team-that-is-taking-on-google",
     "image_height" : 250,
     "text" : "Google has hundreds of researchers working on visual machine perception. Pinterest has a fraction of that. Here’s how the pinning service could still win the race to master visual search.",
     "ts" : 1513004403,
     "service_icon" : "https://www.fastcodesign.com/apple-touch-icon.png?v=2",
     "fallback" : "Co.Design: Inside Pinterest’s 12-Person AI Team That Is Taking On Google",
     "image_bytes" : 321483,
     "image_url" : "https://images.fastcompany.net/image/upload/w_1280,f_auto,q_auto,fl_lossy/wp-cms/uploads/sites/4/2017/12/p-1-pinterest-ai-deep-dive.jpg",
     "title" : "Inside Pinterest’s 12-Person AI Team That Is Taking On Google",
     "image_width" : 444,
     "service_name" : "Co.Design"
  }
  """).getOrElse(Json.Null)

  val fileshared : io.circe.Json = parse("""
     {
       "thumb_360" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_360.jpg",
       "thumb_720_h" : 332,
       "thumb_960_w" : 960,
       "public_url_shared" : false,
       "username" : "",
       "url_private" : "https://files.slack.com/files-pri/T024Z5MQM-F8CMV2GTH/image_uploaded_from_ios.jpg",
       "thumb_64" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_64.jpg",
       "user" : "U024ZH7HL",
       "mode" : "hosted",
       "ims" : [],
       "groups" : [],
       "thumb_1024_w" : 1024,
       "created" : 1512717302,
       "image_exif_rotation" : 1,
       "display_as_bot" : false,
       "thumb_480_w" : 480,
       "thumb_960" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_960.jpg",
       "thumb_800" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_800.jpg",
       "thumb_1024_h" : 473,
       "channels" : [
          "C024Z5MQT"
       ],
       "thumb_80" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_80.jpg",
       "pretty_type" : "JPEG",
       "thumb_720" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_720.jpg",
       "thumb_1024" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_1024.jpg",
       "comments_count" : 5,
       "mimetype" : "image/jpeg",
       "permalink" : "https://donkey.slack.com/files/U024ZH7HL/F8CMV2GTH/image_uploaded_from_ios.jpg",
       "thumb_360_h" : 166,
       "thumb_480" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_480.jpg",
       "thumb_480_h" : 222,
       "is_public" : true,
       "original_h" : 637,
       "filetype" : "jpg",
       "thumb_960_h" : 443,
       "editable" : false,
       "original_w" : 1380,
       "thumb_360_w" : 360,
       "permalink_public" : "https://slack-files.com/T024Z5MQM-F8CMV2GTH-e2fa366d26",
       "thumb_160" : "https://files.slack.com/files-tmb/T024Z5MQM-F8CMV2GTH-779e71594f/image_uploaded_from_ios_160.jpg",
       "thumb_800_w" : 800,
       "title" : "Left is Singapore, right is China. The survey question is, \"I am comfortable with the idea of artificial intelligence/machines acting as...\"",
       "thumb_720_w" : 720,
       "thumb_800_h" : 369,
       "timestamp" : 1512717302,
       "size" : 429223,
       "external_type" : "",
       "id" : "F8CMV2GTH",
       "url_private_download" : "https://files.slack.com/files-pri/T024Z5MQM-F8CMV2GTH/download/image_uploaded_from_ios.jpg",
       "is_external" : false,
       "name" : "Image uploaded from iOS.jpg"
    }
    """).getOrElse(Json.Null)

  val filecomments = parse("""
  "file": {
      "id": "F92N0BLHJ",
      "created": 1517547101,
      "timestamp": 1517547101,
      "name": "Image uploaded from iOS.jpg",
      "title": "My attempt on the green tea latte :joy:",
      "mimetype": "image/jpeg",
      "filetype": "jpg",
      "pretty_type": "JPEG",
      "user": "U8M0NV2H2",
      "editable": false,
      "size": 6187953,
      "mode": "hosted",
      "is_external": false,
      "external_type": "",
      "is_public": true,
      "public_url_shared": false,
      "display_as_bot": false,
      "username": "",
      "url_private": "https://files.slack.com/files-pri/T024Z5MQM-F92N0BLHJ/image_uploaded_from_ios.jpg",
      "url_private_download": "https://files.slack.com/files-pri/T024Z5MQM-F92N0BLHJ/download/image_uploaded_from_ios.jpg",
      "thumb_64": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_64.jpg",
      "thumb_80": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_80.jpg",
      "thumb_360": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_360.jpg",
      "thumb_360_w": 270,
      "thumb_360_h": 360,
      "thumb_480": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_480.jpg",
      "thumb_480_w": 360,
      "thumb_480_h": 480,
      "thumb_160": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_160.jpg",
      "thumb_720": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_720.jpg",
      "thumb_720_w": 540,
      "thumb_720_h": 720,
      "thumb_800": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_800.jpg",
      "thumb_800_w": 800,
      "thumb_800_h": 1067,
      "thumb_960": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_960.jpg",
      "thumb_960_w": 720,
      "thumb_960_h": 960,
      "thumb_1024": "https://files.slack.com/files-tmb/T024Z5MQM-F92N0BLHJ-4167d81090/image_uploaded_from_ios_1024.jpg",
      "thumb_1024_w": 768,
      "thumb_1024_h": 1024,
      "image_exif_rotation": 1,
      "original_w": 3024,
      "original_h": 4032,
      "permalink": "https://nugit.slack.com/files/U8M0NV2H2/F92N0BLHJ/image_uploaded_from_ios.jpg",
      "permalink_public": "https://slack-files.com/T024Z5MQM-F92N0BLHJ-e62166f5b9",
      "channels": [
      "C024Z5MQT"
      ],
      "groups": [],
      "ims": [],
  }
  """).getOrElse(Json.Null)
}

