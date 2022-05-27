package com.acme.web.test.app

import com.acme.web.test.core.Page
import com.acme.web.test.data.User
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

class SignInPage : Page {
  @FindBy(tagName = "app-sign-in-container")
  var rootWebElement: WebElement? = null

  override val waitCondition: ExpectedCondition<WebElement>
    get() = ExpectedConditions.visibilityOf(rootWebElement)

  fun signIn(user: User) {

  }
}
