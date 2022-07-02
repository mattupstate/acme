package com.acme.app.web.test.specs

import com.acme.app.web.test.data.fixture
import com.acme.app.web.test.email.kratos.KratosVerifyEmailContent
import com.acme.app.web.test.email.mailhog.withMailhogApp
import com.acme.app.web.test.kotest.WebDriverExtension
import com.acme.app.web.test.model.RegisterPage
import com.acme.app.web.test.model.withAcmeWebApp
import com.acme.selenium.switchToRemainingWindow
import com.acme.selenium.wait
import com.acme.selenium.windowsAreVisible
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FeatureSpec
import kotlin.time.Duration.Companion.seconds

class RegistrationTests : FeatureSpec({

  val driver = install(WebDriverExtension())

  feature("Password method") {
    scenario("Happy path") {
      val fixture = fixture<RegisterPage.RegistrationValues>()

      withAcmeWebApp(driver) {
        goToRegisterPage {
          fillAndSubmitRegistrationForm(fixture)
        }
      }

      withMailhogApp(driver) {
        navigateToInbox {
          openEmail(fixture.emailAddress, KratosVerifyEmailContent.SUBJECT)

          withEmailMessageContent<KratosVerifyEmailContent> {
            clickVerificationLink()
          }
        }
      }

      driver.wait atMost 2.seconds until 2.windowsAreVisible
      driver.close()
      driver.switchToRemainingWindow()

      withAcmeWebApp(driver) {
        expectSignInPage {
          expectVerificationMessage()
          signIn(fixture.emailAddress, fixture.password)
        }

        expectRootPage {}
      }
    }
  }
})
