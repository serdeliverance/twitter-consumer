package com.sytac.twitter.utils

import akka.Done
import akka.actor.ActorSystem
import akka.stream.KillSwitch
import akka.stream.scaladsl.Source

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object CronUtils {

  def scheduledShutdown(
      finiteDuration: FiniteDuration,
      killSwitch: KillSwitch
    )(implicit system: ActorSystem
    ): Future[Done] =
    Source.single(Done).delay(finiteDuration).runForeach { _ =>
      system.log.info(s"Stream has been running for more than ${finiteDuration._1} seconds. Killing stream")
      killSwitch.shutdown()
    }
}
