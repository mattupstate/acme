package com.acme.app.web.test.app

import com.acme.app.web.test.core.Page
import com.acme.app.web.test.core.buttonText
import com.acme.app.web.test.core.formControlByName
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RegisterPage(driver: WebDriver) : Page(driver) {
  override val rootLocator: By = By.tagName("app-register-container")

  private val formContentCard by lazy {
    FormContentCard(root.findElement(FormContentCard.LOCATOR))
  }

  fun fillAndSubmitRegistrationForm(values: RegistrationValues, waitAtMost: Duration = 10.seconds) {
    with(formContentCard) {
      emailInput.sendKeys(values.emailAddress)
      passwordInput.sendKeys(values.password)
      givenNameInput.sendKeys(values.firstName)
      familyNameInput.sendKeys(values.lastName)
    }

    with(formContentCard) {
      registerButton.click()
    }

    await atMost waitAtMost until presenceOfElementLocated(
      ThankYouContentCard.LOCATOR
    )
  }

  data class RegistrationValues(
    val emailAddress: String,
    val password: String,
    val firstName: String,
    val lastName: String,
  )

  class FormContentCard(private val root: WebElement) {
    companion object {
      val LOCATOR: By = By.name("form-card-content")
    }

    internal val emailInput by lazy {
      root.findElement(formControlByName("email"))
    }

    internal val passwordInput by lazy {
      root.findElement(formControlByName("password"))
    }

    internal val givenNameInput by lazy {
      root.findElement(formControlByName("givenName"))
    }

    internal val familyNameInput by lazy {
      root.findElement(formControlByName("familyName"))
    }

    internal val registerButton by lazy {
      root.findElement(buttonText("Register"))
    }
  }

  class ThankYouContentCard {
    companion object {
      val LOCATOR: By = By.name("thankyou-card-content")
    }
  }
}
