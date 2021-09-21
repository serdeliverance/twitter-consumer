package com.sytac.twitter.tweetprocessing

import akka.actor.{Actor, ActorLogging, Props}
import com.sytac.twitter.tweetprocessing.ReportGenerator.generateReportAndStatistics
import com.sytac.twitter.domain.Tweet

import java.time.ZonedDateTime

/**
  * Because the need of grouping by user and proving some order of the output data, we used an intermediate actor
  * for storing all these state during the streaming phase. When the stream is finished, the GenerateReport message is send
  * to this actor, and then the report and statistics are generated [[ReportGenerator]] convenient functions
  */
class ReportGeneratorMiddleware extends Actor with ActorLogging {

  import ReportGeneratorMiddleware._

  implicit val system           = context.system
  implicit val executionContext = system.dispatcher

  override def receive: Receive = initial()

  def initial() = behavior(List.empty)

  def behavior(tweets: List[Tweet], firstTweetProcessedAt: Option[ZonedDateTime] = None): Receive = {
    case Store(tweet) =>
      log.info(s"Storing tweet: $tweet")
      sender() ! Ack
      context.become(behavior(tweet :: tweets, getFirstProcessingTime(firstTweetProcessedAt)))

    case GenerateReport =>
      log.info("Generating report...")
      val result = generateReportAndStatistics(tweets, firstTweetProcessedAt)

      result.onComplete { _ =>
        log.info("Process finished. Shutting down system")
        system.terminate()
      }

    case StreamInitialized =>
      sender() ! Ack

    case StreamCompleted =>
      sender() ! Ack
      self ! GenerateReport

    case StreamFailure(ex) =>
      log.error(ex, "Processing failed!")
  }

  private def getFirstProcessingTime(maybeFirstTweetProcessedAt: Option[ZonedDateTime]) =
    maybeFirstTweetProcessedAt.orElse(Some(ZonedDateTime.now))
}

object ReportGeneratorMiddleware {

  // protocol
  case class Store(tweet: Tweet)
  case object GenerateReport
  case object Flush

  // stream specific
  case object Ack
  case object StreamInitialized
  case object StreamCompleted
  case class StreamFailure(ex: Throwable)

  def apply() = Props(new ReportGeneratorMiddleware())
}
