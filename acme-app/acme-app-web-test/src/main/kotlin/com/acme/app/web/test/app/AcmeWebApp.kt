package com.acme.app.web.test.app

import com.acme.app.web.test.core.AppObject
import com.acme.app.web.test.core.expectPage
import com.acme.app.web.test.core.gotoPage
import io.kotest.matchers.string.shouldContain
import org.openqa.selenium.By
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

  fun goToRegisterPage(block: RegisterPage.() -> Unit): Unit =
    block(gotoPage(RegisterPage::class))

  fun gotoToSignInPage(block: SignInPage.() -> Unit): Unit =
    block(gotoPage(SignInPage::class))

  fun gotoRootPage(block: RootPage.() -> Unit): Unit =
    block(gotoPage(RootPage::class))

  fun expectSignInPage(block: SignInPage.() -> Unit): Unit =
    block(expectPage(SignInPage::class))

  fun expectRootPage(block: RootPage.() -> Unit): Unit =
    block(expectPage(RootPage::class))

  fun expectSnackBarMessage(message: String) {
    driver.findElement(By.tagName("snack-bar-container")).text shouldContain message
  }

  //
  // fun signIn(user: User) =
  //   waitForPage(SignInPage::class, Duration.ofSeconds(5))
}

fun <T> withAcmeWebApp(rootUrl: String, driver: WebDriver, block: AcmeWebApp.() -> T): T =
  block(AcmeWebApp(rootUrl, driver))

fun <T> withAcmeWebApp(driver: WebDriver, block: AcmeWebApp.() -> T): T =
  withAcmeWebApp(System.getProperty("acme.app.web.url"), driver, block)

