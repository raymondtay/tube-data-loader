package tube.dataloader.core

import tube.dataloader.models._



// Session represents the fictitious session objects that exist for the
// duration of the calls - the main purpose is to simulate the pagination of
// slack's own data
//


object Session {

  def apply(users: List[User])(chunkSize : Int) = {
    if (users.size == chunkSize) {
      Array(Users(true, offset = "ABC33F", users, cache_ts = "0", None))
    } else {
      var page = 0
      val data =
        users.
          sliding(chunkSize, chunkSize).
          toArray.
          map(chunk ⇒ { page += 1; Users(true, offset = "ABC33F", chunk, cache_ts = "0", Some(ResponseData(s"$page")))})
      data.update(data.size - 1, data(data.size - 1).copy(response_metadata = None))
      data
    }
  }

  def apply(channels: List[Channel])(chunkSize : Int) = {
    if (channels.size == chunkSize) {
      Array(Channels(true, channels, None))
    } else {
      var page = 0
      val data =
        channels.
          sliding(chunkSize, chunkSize).
          toArray.
          map(chunk ⇒ { page += 1; Channels(true, chunk, Some(ResponseData(s"$page")))})
      data.update(data.size - 1, data(data.size - 1).copy(response_metadata = None))
      data
    }
  }

  def apply(messageGens: cats.data.NonEmptyList[List[org.scalacheck.Gen[MessageTypes]]])(chunkSize : Int) = {
    import tube.dataloader.models.pure

    val messages : List[org.scalacheck.Gen[MessageTypes]] = messageGens.toList.flatMap(e ⇒ e)

    if (messages.size == chunkSize) {
      Array(Messages(true, messages.map(generator ⇒ generator.sample.get), None))
    } else {
      var page = 0
      val data =
        messages.
          map(generator ⇒ generator.sample.get).
          sliding(chunkSize, chunkSize).
          toArray.
          map(chunk ⇒ { page += 1; Messages(true, chunk, Some(ResponseData(s"$page")))})
      data.update(data.size - 1, data(data.size - 1).copy(response_metadata = None))
      data
    }
  }
}


