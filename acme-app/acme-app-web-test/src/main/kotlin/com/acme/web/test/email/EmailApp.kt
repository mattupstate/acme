package com.acme.web.test.email

import com.acme.web.test.email.mailhog.MailhogApp
import org.openqa.selenium.WebDriver

interface EmailApp {
  fun openVerifyEmailMessageAndClickLink(recipient: String)
}

fun <T> withMailhogApp(rootUrl: String, driver: WebDriver, block: EmailApp.() -> T): T =
  block(MailhogApp(rootUrl, driver))

fun <T> withEmailApp(driver: WebDriver, block: EmailApp.() -> T): T =
  withMailhogApp(System.getProperty("acme.app.mailhog.url"), driver, block)

