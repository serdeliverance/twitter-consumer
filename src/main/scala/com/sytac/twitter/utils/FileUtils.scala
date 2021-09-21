package com.sytac.twitter.utils

import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import scala.concurrent.Future

object FileUtils {

  def saveOnFile(fileName: String, message: String)(implicit system: ActorSystem): Future[IOResult] = {
    val path = Paths.get(fileName)
    Source
      .single(message)
      .map(line => ByteString(line + System.lineSeparator()))
      .runWith(FileIO.toPath(path, Set(WRITE, APPEND)))
  }
}
