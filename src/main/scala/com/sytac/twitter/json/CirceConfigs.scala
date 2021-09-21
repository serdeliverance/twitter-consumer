package com.sytac.twitter.json

import io.circe.Printer
import io.circe.generic.extras.Configuration

trait CirceConfigs {
  implicit val customPrinter: Printer      = Printer.noSpaces.copy(dropNullValues = true)
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
}
