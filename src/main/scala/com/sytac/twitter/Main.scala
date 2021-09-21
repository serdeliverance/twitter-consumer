package com.sytac.twitter

import akka.actor.ActorSystem
import com.sytac.twitter.auth.TwitterAuthentication.httpRequestFactory
import com.sytac.twitter.tweetprocessing.TweetsProcessor.{createTweetSource, processTweets}
import com.typesafe.config.ConfigFactory

object Main extends App {

  implicit val system           = ActorSystem("BieberTweets")
  implicit val executionContext = system.dispatcher

  val config = ConfigFactory.load()

  val consumerKey    = config.getString("twitter.consumer-key")
  val consumerSecret = config.getString("twitter.consumer-secret")

  val requestFactory = httpRequestFactory()

  val tweetSource = createTweetSource(requestFactory)

  processTweets(tweetSource)
}
