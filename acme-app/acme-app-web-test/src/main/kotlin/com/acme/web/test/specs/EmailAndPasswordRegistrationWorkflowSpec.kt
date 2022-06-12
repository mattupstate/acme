package com.acme.web.test.specs

import com.acme.web.test.app.RegisterPage
import com.acme.web.test.app.withAcmeWebApp
import com.acme.web.test.data.fixture
import com.acme.web.test.email.withEmailApp
import io.github.bonigarcia.seljup.SeleniumJupiter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebDriver

@ExtendWith(SeleniumJupiter::class)
class EmailAndPasswordRegistrationWorkflowSpec {

  @Test
  fun happyPath(driver: WebDriver) {
    val registrationFixture = fixture<RegisterPage.RegistrationValues>()

    withAcmeWebApp(driver) {
      navigateToRegisterPage {
        fillAndSubmitRegistrationForm(registrationFixture)
      }
    }.also {
      withEmailApp(driver) {
        openVerifyEmailMessageAndClickLink(
          registrationFixture.emailAddress
        )
      }
    }
  }
}
