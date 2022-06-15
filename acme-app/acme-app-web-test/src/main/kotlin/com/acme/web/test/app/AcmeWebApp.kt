package com.acme.web.test.app

import com.acme.web.test.core.AppObject
import com.acme.web.test.core.navigate
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

  fun <T> navigateToRegisterPage(block: RegisterPage.() -> T): T =
    block(navigate(RegisterPage::class))

  fun <T> navigateToSignInPage(block: SignInPage.() -> T): T =
    block(navigate(SignInPage::class))
  //
  // fun signIn(user: User) =
  //   waitForPage(SignInPage::class, Duration.ofSeconds(5))
}

fun <T> withAcmeWebApp(rootUrl: String, driver: WebDriver, block: AcmeWebApp.() -> T): T =
  block(AcmeWebApp(rootUrl, driver))

fun <T> withAcmeWebApp(driver: WebDriver, block: AcmeWebApp.() -> T): T =
  withAcmeWebApp(System.getProperty("acme.app.web.url"), driver, block)

