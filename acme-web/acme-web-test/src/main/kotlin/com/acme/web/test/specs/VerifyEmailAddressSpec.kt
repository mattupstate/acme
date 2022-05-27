package com.acme.web.test.specs

import com.acme.web.test.app.withAcmeApp
import com.acme.web.test.core.waitForInvisibilityOf
import com.acme.web.test.mailhog.withMailhogApp
import org.junit.jupiter.api.TestTemplate
import org.openqa.selenium.WebDriver

class VerifyEmailAddressSpec : Spec() {

  @TestTemplate
  fun `should send email with link and return back to application`(driver: WebDriver) {
    withAcmeApp(driver) {
      navigateToRegisterPage {
        val user = fillAndSubmitRegistrationForm()

        waitForInvisibilityOf(rootWebElement)

        withMailhogApp(driver) {
          navigateToDashboard {
            clickMessageListItemContaining(user.email, "Please verify your email address")
          }
        }
      }
    }
  }
}
