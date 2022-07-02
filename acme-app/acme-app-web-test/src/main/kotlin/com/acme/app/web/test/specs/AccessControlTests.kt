package com.acme.app.web.test.specs

import com.acme.app.web.test.app.withAcmeWebApp
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.ShouldSpec
import org.openqa.selenium.TimeoutException

class AccessControlTests : ShouldSpec({
  val driver = install(WebDriverExtension())

  context("Unauthenticated users") {
    should("be redirected to the the sign-in page when attempting to access the application") {
      withAcmeWebApp(driver) {
        shouldThrow<TimeoutException> {
          gotoRootPage { }
        }
        expectSignInPage { }
        expectSnackBarMessage(
          "You must be logged in to access this page."
        )
      }
    }
  }
})
