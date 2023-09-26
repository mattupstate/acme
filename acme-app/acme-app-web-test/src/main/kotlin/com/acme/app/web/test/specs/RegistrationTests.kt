package com.acme.app.web.test.specs

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
import net.datafaker.Faker
import kotlin.time.Duration.Companion.seconds

val faker = Faker()

class RegistrationTests : FeatureSpec({

  val driver = install(WebDriverExtension())

  feature("Password method") {
    scenario("Happy path") {
      val fixture = RegisterPage.RegistrationValues(
        emailAddress = faker.internet().emailAddress(),
        password = faker.internet().password(12, 24, true),
        firstName = faker.name().firstName(),
        lastName = faker.name().lastName(),
      )

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
