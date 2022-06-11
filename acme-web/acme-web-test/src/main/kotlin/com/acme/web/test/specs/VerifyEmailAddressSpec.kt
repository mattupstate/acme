package com.acme.web.test.specs

import com.acme.web.test.app.withAcmeApp
import com.acme.web.test.core.waitForInvisibilityOf
import com.acme.web.test.mailhog.withMailhogApp
import io.github.bonigarcia.seljup.DriverCapabilities
import io.github.bonigarcia.seljup.SeleniumJupiter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

@ExtendWith(SeleniumJupiter::class)
class VerifyEmailAddressSpec {

  @DriverCapabilities
  val capabilities = DesiredCapabilities().apply {
    setAcceptInsecureCerts(true)
  }

  @Test
  fun testThing(driver: WebDriver) {
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
