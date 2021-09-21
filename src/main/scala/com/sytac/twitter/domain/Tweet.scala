package com.sytac.twitter.domain

import com.sytac.twitter.json.CirceConfigs
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}

case class Tweet(id: Long, idStr: String, createdAt: String, text: String, user: User)

object Tweet extends CirceConfigs {

  implicit lazy val tweetMessageDecoder: Decoder[Tweet] = deriveConfiguredDecoder[Tweet]
  implicit lazy val tweetMessageEncoder: Encoder[Tweet] = deriveConfiguredEncoder[Tweet]
}
