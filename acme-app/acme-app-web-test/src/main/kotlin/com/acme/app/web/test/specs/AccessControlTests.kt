package com.acme.app.web.test.specs

import com.acme.app.web.test.kotest.WebDriverExtension
import com.acme.app.web.test.model.withAcmeWebApp
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.ShouldSpec
import org.openqa.selenium.TimeoutException

class AccessControlTests : ShouldSpec({
  val driver = install(WebDriverExtension())

  context("Unauthenticated users") {
    should("be redirected to the the sign-in page") {
      withAcmeWebApp(driver) {
        shouldThrow<TimeoutException> {
          goToRootPage { }
        }
        expectSignInPage { }
        expectSnackBarMessage(
          "You must be logged in to access this page."
        )
      }
    }
  }
})
