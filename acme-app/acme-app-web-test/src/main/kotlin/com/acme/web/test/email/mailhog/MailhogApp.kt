package com.acme.web.test.email.mailhog

import com.acme.web.test.core.AppObject
import com.acme.web.test.core.navigate
import com.acme.web.test.email.EmailApp
import org.openqa.selenium.WebDriver
import kotlin.reflect.KClass

class MailhogApp(root: String, driver: WebDriver) : AppObject(root, URL_MAP, driver), EmailApp {
  companion object {
    val URL_MAP: Map<KClass<out Any>, String> = mapOf(
      DashboardPage::class to "/"
    )
  }

  private fun navigateToDashboard(block: DashboardPage.() -> Unit) =
    navigate(DashboardPage::class).apply(block)

  override fun openVerifyEmailMessageAndClickLink(recipient: String) {
    navigateToDashboard {
      clickMessageListItemContaining(recipient)
    }
  }
}
