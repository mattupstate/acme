package com.acme.app.web.test.email.mailhog

import com.acme.app.web.test.core.AppObject
import com.acme.app.web.test.core.navigate
import org.openqa.selenium.WebDriver
import kotlin.reflect.KClass

class MailhogApp(root: String, driver: WebDriver) : AppObject(root, URL_MAP, driver) {
  companion object {
    val URL_MAP: Map<KClass<out Any>, String> = mapOf(
      RootPage::class to "/"
    )
  }

  fun navigateToInbox(block: RootPage.() -> Unit) {
    with(navigate(RootPage::class)) {
      navMenu.inboxButton.click()
      apply(block)
    }
  }
}

fun <T> withMailhogApp(driver: WebDriver, block: MailhogApp.() -> T): T =
  block(MailhogApp(System.getProperty("acme.app.mailhog.url"), driver))

