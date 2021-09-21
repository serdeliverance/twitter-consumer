package com.sytac.twitter.domain

import com.sytac.twitter.json.CirceConfigs
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}

case class User(id: Long, idStr: String, name: String, screenName: String, createdAt: String)

object User extends CirceConfigs {
  implicit lazy val userDecoder: Decoder[User] = deriveConfiguredDecoder[User]
  implicit lazy val userEncoder: Encoder[User] = deriveConfiguredEncoder[User]
}
