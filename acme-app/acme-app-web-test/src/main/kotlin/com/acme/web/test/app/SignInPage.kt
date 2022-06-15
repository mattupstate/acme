package com.acme.web.test.app

import com.acme.web.test.core.Page
import com.acme.web.test.core.buttonText
import com.acme.web.test.core.formControlByName
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

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

  fun signIn(emailAddress: String, password: String) {
    usernameInput.sendKeys(emailAddress)
    passwordInput.sendKeys(password)
    signInButton.click()
  }
}
