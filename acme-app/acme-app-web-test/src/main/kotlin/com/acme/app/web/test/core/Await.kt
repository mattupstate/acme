package com.acme.app.web.test.core

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class Await(private val driver: WebDriver) {
  infix fun atMost(duration: Duration) = AwaitCondition(driver, duration)

  class AwaitCondition(private val driver: WebDriver, private val duration: Duration) {
    infix fun until(condition: ExpectedCondition<*>) {
      WebDriverWait(driver, duration.toJavaDuration()).until(condition)
    }
  }
}
