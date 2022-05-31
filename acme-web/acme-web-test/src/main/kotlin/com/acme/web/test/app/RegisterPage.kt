package com.acme.web.test.app

import com.acme.web.test.core.Page
import com.acme.web.test.core.buttonText
import com.acme.web.test.core.formControl
import com.acme.web.test.data.randomEmailAddress
import com.acme.web.test.data.randomLetters
import com.acme.web.test.data.randomLettersAndNumbers
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

class RegisterPage : Page {
  @FindBy(tagName = "app-register-container")
  val rootWebElement: WebElement? = null
  val emailInput by lazy { rootWebElement!!.findElement(formControl("email")) }
  val passwordInput by lazy { rootWebElement!!.findElement(formControl("password")) }
  val givenNameInput by lazy { rootWebElement!!.findElement(formControl("givenName")) }
  val familyNameInput by lazy { rootWebElement!!.findElement(formControl("familyName")) }
  val registerButton by lazy { rootWebElement!!.findElement(buttonText("Register")) }

  override val waitCondition: ExpectedCondition<WebElement>
    get() = ExpectedConditions.visibilityOf(rootWebElement)

  fun submitRegistrationForm() {
    registerButton.click()
  }

  fun fillRegistrationForm(email: String, password: String, givenName: String, familyName: String) {
    emailInput.sendKeys(email)
    passwordInput.sendKeys(password)
    givenNameInput.sendKeys(givenName)
    familyNameInput.sendKeys(familyName)
  }

  fun fillRegistrationForm(values: RegistrationValues) =
    fillRegistrationForm(values.email, values.password, values.givenName, values.familyName)

  fun fillRegistrationForm() =
    RegistrationValues(
      randomEmailAddress(),
      randomLettersAndNumbers(20),
      randomLetters(6),
      randomLetters(10)
    ).also {
      fillRegistrationForm(it)
    }

  fun fillAndSubmitRegistrationForm(email: String, password: String, firstName: String, lastName: String) {
    fillRegistrationForm(email, password, firstName, lastName)
    submitRegistrationForm()
  }

  fun fillAndSubmitRegistrationForm() =
    fillRegistrationForm().also {
      submitRegistrationForm()
    }

  data class RegistrationValues(
    val email: String = "",
    val password: String = "",
    val givenName: String = "",
    val familyName: String = "",
  )
}
