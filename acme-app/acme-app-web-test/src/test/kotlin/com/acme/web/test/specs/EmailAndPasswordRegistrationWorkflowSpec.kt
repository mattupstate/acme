package com.acme.web.test.specs

import com.acme.web.test.AcmeAppWebTestConfig.Companion.selenium
import com.acme.web.test.app.RegisterPage
import com.acme.web.test.app.withAcmeWebApp
import com.acme.web.test.core.waitFor
import com.acme.web.test.data.fixture
import com.acme.web.test.email.kratos.KratosVerifyEmailContent
import com.acme.web.test.email.mailhog.withMailhogApp
import io.kotest.core.spec.style.ShouldSpec
import org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe

class EmailAndPasswordRegistrationWorkflowSpec : ShouldSpec({

  val driver = selenium.drivers.first()

  should("afford a new user to register and login after they verify their email address") {
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
          )
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

  // selenium.drivers.forEach {
  //   include(happyPath(it))
  // }
})
