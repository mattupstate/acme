package com.acme.app.web.test.app

import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class PracticesPage {
  @FindBy(id = "practice-name")
  var practiceNameInput: WebElement? = null
}
