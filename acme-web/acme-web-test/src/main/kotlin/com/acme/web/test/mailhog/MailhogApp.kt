package com.acme.web.test.mailhog

import com.acme.web.test.core.AppObject
import com.acme.web.test.core.navigate
import org.openqa.selenium.WebDriver
import kotlin.reflect.KClass

class MailhogApp(root: String, driver: WebDriver) : AppObject(root, URL_MAP, driver) {
  companion object {
    val URL_MAP: Map<KClass<out Any>, String> = mapOf(
      DashboardPage::class to "/"
    )
  }

  fun navigateToDashboard(block: DashboardPage.() -> Unit) =
    navigate(DashboardPage::class).apply(block)
}

fun withMailhogApp(rootUrl: String, driver: WebDriver, block: MailhogApp.() -> Unit) {
  MailhogApp(rootUrl, driver).apply(block)
}

fun withMailhogApp(driver: WebDriver, block: MailhogApp.() -> Unit) {
  withMailhogApp(System.getProperty("acme.app.mailhog.url"), driver, block)
}
