package com.sytac.twitter.utils

import java.time.ZonedDateTime

object TimeUtils {

  def subtractionInSeconds(aDateTime: ZonedDateTime, anotherDateTime: ZonedDateTime): Long =
    aDateTime.toEpochSecond - anotherDateTime.toEpochSecond

  def timestamp(dateTime: ZonedDateTime): String = {
    val year   = dateTime.getYear
    val month  = formatTwoDigits(dateTime.getMonthValue)
    val day    = formatTwoDigits(dateTime.getDayOfMonth)
    val hour   = formatTwoDigits(dateTime.getHour)
    val minute = formatTwoDigits(dateTime.getMinute)
    s"$year$month$day$hour$minute"
  }

  private def formatTwoDigits(value: Int) =
    if (value < 10) s"0$value" else value.toString
}
