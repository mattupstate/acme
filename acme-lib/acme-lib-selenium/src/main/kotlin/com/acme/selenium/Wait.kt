package com.acme.selenium

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class Wait(private val driver: WebDriver) {
  infix fun atMost(duration: Duration) = WaitCondition(driver, duration)

  class WaitCondition(private val driver: WebDriver, private val duration: Duration) {
    infix fun until(condition: ExpectedCondition<*>) {
      WebDriverWait(driver, duration.toJavaDuration()).until(condition)
    }
  }
}
