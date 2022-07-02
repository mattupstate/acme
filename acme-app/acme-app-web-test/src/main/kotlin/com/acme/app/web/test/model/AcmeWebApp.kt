package com.acme.app.web.test.model

import com.acme.selenium.AppObject
import io.kotest.matchers.string.shouldContain
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.net.URL
import kotlin.reflect.KClass

class AcmeWebApp(rootUrl: String, driver: WebDriver) : AppObject(driver) {

  override val rootUrl: URL = URL(rootUrl)

  override val pages: Map<KClass<out Any>, String> = mapOf(
    RootPage::class to "/",
    SignInPage::class to "/sign-in",
    RegisterPage::class to "/register",
  )

  private val snackBarContainer: WebElement by lazy {
    driver.findElement(By.tagName("snack-bar-container"))
  }

  fun goToRegisterPage(block: RegisterPage.() -> Unit): Unit =
    block(gotoPage(RegisterPage::class))

  fun goToSignInPage(block: SignInPage.() -> Unit): Unit =
    block(gotoPage(SignInPage::class))

  fun goToRootPage(block: RootPage.() -> Unit): Unit =
    block(gotoPage(RootPage::class))

  fun expectSignInPage(block: SignInPage.() -> Unit): Unit =
    block(expectPage(SignInPage::class))

  fun expectRootPage(block: RootPage.() -> Unit): Unit =
    block(expectPage(RootPage::class))

  fun expectSnackBarMessage(message: String) {
    snackBarContainer.text shouldContain message
  }
}

fun <T> withAcmeWebApp(rootUrl: String, driver: WebDriver, block: AcmeWebApp.() -> T): T =
  block(AcmeWebApp(rootUrl, driver))

fun <T> withAcmeWebApp(driver: WebDriver, block: AcmeWebApp.() -> T): T =
  withAcmeWebApp(System.getProperty("acme.app.web.url"), driver, block)

