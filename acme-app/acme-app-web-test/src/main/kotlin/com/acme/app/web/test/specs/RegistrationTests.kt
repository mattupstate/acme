package com.acme.app.web.test.specs

import com.acme.app.web.test.app.RegisterPage
import com.acme.app.web.test.app.withAcmeWebApp
import com.acme.app.web.test.core.switchToRemainingWindow
import com.acme.app.web.test.core.wait
import com.acme.app.web.test.core.windowsAreVisible
import com.acme.app.web.test.data.fixture
import com.acme.app.web.test.email.kratos.KratosVerifyEmailContent
import com.acme.app.web.test.email.mailhog.withMailhogApp
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FeatureSpec
import kotlin.time.Duration.Companion.seconds

class RegistrationTests : FeatureSpec({

  val driver = install(WebDriverExtension())

  feature("Password method") {
    scenario(
      """
      A new users requests to register a new account.
      They receive and click the email verification link.
      They are redirected to the sign in page.
      They are informed that they successfully verified their email address.
      They sign in using the password specified during registration.
      They land on the dashboard page.
    """.trimIndent()
    ) {
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
