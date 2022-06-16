package com.acme.web.test.app

import com.acme.web.test.core.Page
import com.acme.web.test.core.buttonText
import com.acme.web.test.core.formControlByName
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

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

  fun signIn(emailAddress: String, password: String): ExpectedCondition<WebElement> {
    usernameInput.sendKeys(emailAddress)
    passwordInput.sendKeys(password)
    signInButton.click()
    return ExpectedConditions.presenceOfElementLocated(
      By.tagName("app-root-container")
    )
  }
}
