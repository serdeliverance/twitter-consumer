package com.sytac.twitter.utils

object NumberUtils {

  def doubleDivisionWithRounding(dividend: Long, divisor: Long): Double =
    roundToTwoDigits(doubleDivision(dividend, divisor))

  def doubleDivision(dividend: Long, divisor: Long): Double =
    dividend.toDouble / divisor.toDouble

  def roundToTwoDigits(value: Double): Double =
    BigDecimal(value).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
}
