package com.acme.web.app.test.email.mailhog

import com.acme.selenium.AppObject
import org.openqa.selenium.WebDriver
import java.net.URL
import kotlin.reflect.KClass

class MailhogApp(rootUrl: String, driver: WebDriver) : AppObject(driver) {

  override val rootUrl: URL = URL(rootUrl)

  override val pages: Map<KClass<out Any>, String> = mapOf(
    RootPage::class to "/"
  )

  fun navigateToInbox(block: RootPage.() -> Unit) {
    with(gotoPage(RootPage::class)) {
      navMenu.inboxButton.click()
      apply(block)
    }
  }
}

fun <T> withMailhogApp(driver: WebDriver, block: MailhogApp.() -> T): T =
  block(MailhogApp(System.getProperty("acme.app.mailhog.url"), driver))

