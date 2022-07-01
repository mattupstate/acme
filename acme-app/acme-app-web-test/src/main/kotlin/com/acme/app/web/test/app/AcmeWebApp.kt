package com.acme.app.web.test.app

import com.acme.app.web.test.core.AppObject
import com.acme.app.web.test.core.navigate
import org.openqa.selenium.WebDriver

class AcmeWebApp(root: String, driver: WebDriver) : AppObject(root, URL_MAP, driver) {
  companion object {
    val URL_MAP = mapOf(
      RootPage::class to "/",
      SignInPage::class to "/sign-in",
      PracticesPage::class to "/practices",
      RegisterPage::class to "/register",
    )
  }

  fun navigateToRegisterPage(block: RegisterPage.() -> Unit): Unit =
    block(navigate(RegisterPage::class))

  fun navigateToSignInPage(block: SignInPage.() -> Unit): Unit =
    block(navigate(SignInPage::class))

  //
  // fun signIn(user: User) =
  //   waitForPage(SignInPage::class, Duration.ofSeconds(5))
}

fun <T> withAcmeWebApp(rootUrl: String, driver: WebDriver, block: AcmeWebApp.() -> T): T =
  block(AcmeWebApp(rootUrl, driver))

fun <T> withAcmeWebApp(driver: WebDriver, block: AcmeWebApp.() -> T): T =
  withAcmeWebApp(System.getProperty("acme.app.web.url"), driver, block)

