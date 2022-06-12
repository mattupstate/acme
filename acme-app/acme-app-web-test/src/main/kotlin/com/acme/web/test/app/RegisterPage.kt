package com.acme.web.test.app

import com.acme.web.test.core.Page
import com.acme.web.test.core.buttonText
import com.acme.web.test.core.formControl
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

class RegisterPage : Page {
  @FindBy(tagName = "app-register-container")
  val rootWebElement: WebElement? = null

  private val emailInput: WebElement by lazy {
    rootWebElement!!.findElement(formControl("email"))
  }

  private val passwordInput: WebElement by lazy {
    rootWebElement!!.findElement(formControl("password"))
  }

  private val givenNameInput: WebElement by lazy {
    rootWebElement!!.findElement(formControl("givenName"))
  }

  private val familyNameInput: WebElement by lazy {
    rootWebElement!!.findElement(formControl("familyName"))
  }

  private val registerButton: WebElement by lazy {
    rootWebElement!!.findElement(buttonText("Register"))
  }

  override val waitCondition: ExpectedCondition<WebElement>
    get() = ExpectedConditions.visibilityOf(rootWebElement)

  private fun submitRegistrationForm() {
    registerButton.click()
  }

  private fun fillRegistrationForm(email: String, password: String, givenName: String, familyName: String) {
    emailInput.sendKeys(email)
    passwordInput.sendKeys(password)
    givenNameInput.sendKeys(givenName)
    familyNameInput.sendKeys(familyName)
  }

  private fun fillRegistrationForm(values: RegistrationValues) =
    fillRegistrationForm(values.emailAddress, values.password, values.firstName, values.lastName)

  fun fillAndSubmitRegistrationForm(values: RegistrationValues) {
    fillRegistrationForm(values)
    submitRegistrationForm()
  }

  data class RegistrationValues(
    val emailAddress: String,
    val password: String,
    val firstName: String,
    val lastName: String,
  )
}
