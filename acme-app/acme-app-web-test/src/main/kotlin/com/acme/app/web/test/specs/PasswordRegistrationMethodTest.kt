package com.acme.app.web.test.specs

import com.acme.app.web.test.app.RegisterPage
import com.acme.app.web.test.app.withAcmeWebApp
import com.acme.app.web.test.data.fixture
import com.acme.app.web.test.email.kratos.KratosVerifyEmailContent
import com.acme.app.web.test.email.mailhog.withMailhogApp
import io.github.bonigarcia.seljup.SeleniumJupiter
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe
import kotlin.time.Duration.Companion.seconds

@ExtendWith(SeleniumJupiter::class)
class PasswordRegistrationMethodTest {

  @TestTemplate
  fun happyPath(driver: WebDriver) {
    val fixture = fixture<RegisterPage.RegistrationValues>()
    val registerWindow = driver.windowHandle

    withAcmeWebApp(driver) {
      navigateToRegisterPage {
        fillAndSubmitRegistrationForm(fixture)
      }
    }

    withMailhogApp(driver) {
      navigateToInbox {
        openEmail(
          fixture.emailAddress, KratosVerifyEmailContent.SUBJECT
        )

        withEmailMessageContent<KratosVerifyEmailContent> {
          clickVerificationLink()
        }
      }

      await atMost 10.seconds until numberOfWindowsToBe(2)
    }

    driver.switchTo().window(
      driver.windowHandles.minus(registerWindow).first()
    )

    withAcmeWebApp(driver) {
      navigateToSignInPage {
        signIn(fixture.emailAddress, fixture.password)
      }
    }
  }
}
