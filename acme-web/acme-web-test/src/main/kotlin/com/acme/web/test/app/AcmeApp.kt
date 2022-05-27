package com.acme.web.test.app

import com.acme.web.test.core.AppObject
import com.acme.web.test.core.NavigationException
import com.acme.web.test.core.Page
import com.acme.web.test.core.navigate
import com.acme.web.test.core.waitForPage
import com.acme.web.test.data.User
import mu.KotlinLogging
import org.openqa.selenium.WebDriver
import java.time.Duration
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

class AcmeApp(root: String, driver: WebDriver) : AppObject(root, URL_MAP, driver) {
  companion object {
    val URL_MAP = mapOf(
      RootPage::class to "/",
      SignInPage::class to "/sign-in",
      PracticesPage::class to "/practices",
      RegisterPage::class to "/register",
    )
  }

  fun navigateToRegisterPage(block: RegisterPage.() -> Unit) =
    navigate(RegisterPage::class).apply(block)
}

fun AppObject.signIn(user: User) =
  waitForPage(SignInPage::class, Duration.ofSeconds(5)).signIn(user)

fun <P : Page> AppObject.navigateAndMaybeSignIn(
  user: User,
  pageType: KClass<P>,
): P =
  try {
    navigate(pageType)
  } catch (e: NavigationException) {
    logger.debug("Navigation failed. Attempting sign in")
    signIn(user)
    waitForPage(pageType, Duration.ofSeconds(5))
  }

fun withAcmeApp(rootUrl: String, driver: WebDriver, block: AcmeApp.() -> Unit) {
  AcmeApp(rootUrl, driver).apply(block)
}

fun withAcmeApp(driver: WebDriver, block: AcmeApp.() -> Unit) {
  withAcmeApp(System.getProperty("acme.app.web.url"), driver, block)
}
