package com.sytac.twitter.tweetprocessing

import akka.actor.ActorSystem
import akka.stream.KillSwitches
import akka.stream.scaladsl.{JsonFraming, Keep, Sink, Source, StreamConverters}
import akka.{Done, NotUsed}
import com.google.api.client.http.{GenericUrl, HttpRequestFactory}
import com.sytac.twitter.domain.Tweet
import com.sytac.twitter.tweetprocessing.ReportGeneratorMiddleware._
import com.sytac.twitter.utils.CronUtils.scheduledShutdown
import io.circe.jawn.decode

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

object TweetsProcessor {

  val TOPIC               = "bieber"
  val REALTIME_TWEETS_URL = s"https://stream.twitter.com/1.1/statuses/filter.json?track=$TOPIC"

  def processTweets(
      tweetSource: Source[String, NotUsed]
    )(implicit system: ActorSystem,
      executionContext: ExecutionContext
    ): Future[Done] = {
    val reportGeneratorActor = system.actorOf(ReportGeneratorMiddleware())

    val killSwitch = tweetSource
      .viaMat(KillSwitches.single)(Keep.right) // used to shutdown stream after 30 seconds
      .map(jsonStr => decode[Tweet](jsonStr))
      .collect {
        case Right(tweetMessage) => tweetMessage
      }
      .take(100) // to read 100 elements and then finish
      .map(tweetMessage => Store(tweetMessage))
      .to(
        Sink.actorRefWithBackpressure(
          reportGeneratorActor,
          StreamInitialized,
          Ack,
          StreamCompleted,
          (ex: Throwable) => StreamFailure(ex)
        )
      )
      .run()

    scheduledShutdown(30.seconds, killSwitch)
  }

  /**
    * Creates a Source which adapts the http response from twitter realtime api and convert
    * its input stream into an Akka Source.
    *
    */
  def createTweetSource(
      requestFactory: HttpRequestFactory
    )(implicit executionContext: ExecutionContext
    ): Source[String, NotUsed] = {

    val streamedResponse = requestFactory
      .buildGetRequest(new GenericUrl(REALTIME_TWEETS_URL))
      .execute()

    StreamConverters
      .fromInputStream(() => streamedResponse.getContent)
      .via(JsonFraming.objectScanner(Int.MaxValue))
      .map(byteStringLine => byteStringLine.utf8String)
      .mapMaterializedValue(_ => NotUsed)
  }
}
