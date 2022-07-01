package com.acme.app.web.test.email.kratos

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class KratosVerifyEmailContent(private val root: WebElement) {

  fun clickVerificationLink() {
    root.findElement(By.xpath("//a")).click()
  }

  companion object {
    const val SUBJECT = "Please verify your email address"
  }
}
