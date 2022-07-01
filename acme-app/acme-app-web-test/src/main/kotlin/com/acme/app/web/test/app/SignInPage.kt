package com.acme.app.web.test.app

import com.acme.app.web.test.core.Page
import com.acme.app.web.test.core.buttonText
import com.acme.app.web.test.core.formControlByName
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SignInPage(driver: WebDriver) : Page(driver) {
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

  fun signIn(emailAddress: String, password: String, waitAtMost: Duration = 10.seconds) {
    usernameInput.sendKeys(emailAddress)
    passwordInput.sendKeys(password)
    signInButton.click()
    await atMost waitAtMost until ExpectedConditions.presenceOfElementLocated(
      By.tagName("app-root-container")
    )
  }
}
