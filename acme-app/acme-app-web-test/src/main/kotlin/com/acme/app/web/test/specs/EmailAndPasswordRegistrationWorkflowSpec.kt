package com.acme.app.web.test.specs

import com.acme.app.web.test.app.RegisterPage
import com.acme.app.web.test.app.withAcmeWebApp
import com.acme.app.web.test.core.waitFor
import com.acme.app.web.test.data.fixture
import com.acme.app.web.test.email.kratos.KratosVerifyEmailContent
import com.acme.app.web.test.email.mailhog.withMailhogApp
import org.fluentlenium.adapter.kotest.FluentShouldSpec
import org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe
import kotlin.time.Duration.Companion.seconds

class EmailAndPasswordRegistrationWorkflowSpec : FluentShouldSpec({

  should("happy path") {
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
})
