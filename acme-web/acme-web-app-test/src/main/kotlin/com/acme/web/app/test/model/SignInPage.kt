package com.acme.web.app.test.model

import com.acme.selenium.PageObject
import com.acme.selenium.buttonText
import com.acme.selenium.formControlByName
import com.acme.selenium.wait
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.string.shouldContain
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import kotlin.time.Duration.Companion.seconds

class SignInPage(driver: WebDriver) : PageObject(driver) {

  private val logger = KotlinLogging.logger {}

  override val rootLocator: By = By.tagName("app-sign-in-container")

  private val usernameInput by lazy {
    root.findElement(formControlByName("email"))
  }

  private val passwordInput by lazy {
    root.findElement(formControlByName("password"))
  }

  private val signInButton by lazy {
    root.findElement(buttonText("Sign in"))
  }

  private val verificationMessage by lazy {
    root.findElement(By.className("post-verify"))
  }

  fun expectVerificationMessage() {
    driver.wait atMost 1.seconds until ExpectedConditions.visibilityOf(verificationMessage)
    verificationMessage.text shouldContain """
      Thanks for verifying your account. You may now sign-in using your email and password.
    """.trimIndent()
  }

  fun signIn(emailAddress: String, password: String) {
    usernameInput.sendKeys(emailAddress)
    passwordInput.sendKeys(password)
    signInButton.click()
    driver.wait atMost 3.seconds until ExpectedConditions.presenceOfElementLocated(
      By.tagName("app-root-container")
    )
  }
}
