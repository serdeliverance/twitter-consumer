package com.sytac.twitter.tweetprocessing

import akka.Done
import akka.Done.done
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import com.sytac.twitter.domain.Tweet
import com.sytac.twitter.utils.FileUtils.saveOnFile
import com.sytac.twitter.utils.NumberUtils.doubleDivisionWithRounding
import com.sytac.twitter.utils.TimeUtils.{subtractionInSeconds, timestamp}
import io.circe.syntax._

import java.nio.file.Paths
import java.time.ZonedDateTime
import scala.concurrent.{ExecutionContext, Future}

/**
  * This singleton/namespace contains functions needed for generating the required reports and statistics
  */
object ReportGenerator {

  def generateReportAndStatistics(
      tweets: List[Tweet],
      maybeFirstMessageProcessedAt: Option[ZonedDateTime]
    )(implicit system: ActorSystem,
      executionContext: ExecutionContext
    ): Future[Done] =
    for {
      _ <- generateReport(tweets)
      _ <- persistStatistics(tweets, maybeFirstMessageProcessedAt)
    } yield done()

  private def generateReport(tweets: List[Tweet])(implicit system: ActorSystem): Future[IOResult] = {
    val outputFile   = Paths.get(s"bieber-tweets${timestamp(ZonedDateTime.now())}.txt")
    val sortedTweets = sortTweets(tweets)

    Source(sortedTweets)
      .map(tweet => tweet.asJson.noSpaces)
      .map(str => ByteString(str + System.lineSeparator()))
      .runWith(FileIO.toPath(outputFile))
  }

  private def persistStatistics(
      tweets: List[Tweet],
      maybeFirstMessageProcessedAt: Option[ZonedDateTime]
    )(implicit system: ActorSystem
    ): Future[IOResult] = {
    val currentTime = ZonedDateTime.now()
    val tweetsPerSecond = maybeFirstMessageProcessedAt match {
      case Some(firstMessageProcessedAt) if tweets.nonEmpty =>
        doubleDivisionWithRounding(tweets.length, subtractionInSeconds(currentTime, firstMessageProcessedAt))
      case _ => 0
    }
    saveOnFile("statistics.txt", s"Execution at: $currentTime | processing rate: $tweetsPerSecond tweets per second")
  }

  private def sortTweets(tweets: List[Tweet]): List[Tweet] =
    tweets
      .groupBy(_.user.name)
      .map(userTweetsTuple => (userTweetsTuple._1, userTweetsTuple._2.sortBy(_.createdAt)))
      .toList
      .sortBy(_._1)
      .flatMap(_._2)
}
