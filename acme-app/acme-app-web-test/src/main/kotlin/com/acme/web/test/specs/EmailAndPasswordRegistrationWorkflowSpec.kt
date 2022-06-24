package com.acme.web.test.specs

import com.acme.web.test.app.RegisterPage
import com.acme.web.test.app.withAcmeWebApp
import com.acme.web.test.core.waitFor
import com.acme.web.test.data.fixture
import com.acme.web.test.email.kratos.KratosVerifyEmailContent
import com.acme.web.test.email.mailhog.withMailhogApp
import io.github.bonigarcia.seljup.SeleniumJupiter
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe
import kotlin.time.Duration.Companion.seconds

@ExtendWith(SeleniumJupiter::class)
class EmailAndPasswordRegistrationWorkflowSpec {

  @TestTemplate
  fun happyPath(driver: WebDriver) {
    val fixture = fixture<RegisterPage.RegistrationValues>()
    val registerWindow = driver.windowHandle

    withAcmeWebApp(driver) {
      navigateToRegisterPage {
        waitFor(
          fillAndSubmitRegistrationForm(fixture)
        )
      }
    }

    withMailhogApp(driver) {
      navigateToInbox {
        waitFor(
          emailDeliveredCondition(
            fixture.emailAddress, KratosVerifyEmailContent.SUBJECT
          ),
          10.seconds
        )
        waitFor(
          openEmail(
            fixture.emailAddress, KratosVerifyEmailContent.SUBJECT
          )
        )
        withEmailMessageContent<KratosVerifyEmailContent> {
          clickVerificationLink()
        }
        waitFor(
          numberOfWindowsToBe(2)
        )
      }
    }

    driver.switchTo().window(
      driver.windowHandles.minus(registerWindow).first()
    )

    withAcmeWebApp(driver) {
      navigateToSignInPage {
        waitFor(
          signIn(fixture.emailAddress, fixture.password)
        )
      }
    }
  }
}
