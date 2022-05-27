package com.acme.web.test.specs

import io.github.bonigarcia.seljup.DriverCapabilities
import io.github.bonigarcia.seljup.SeleniumJupiter
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.remote.DesiredCapabilities

@ExtendWith(SeleniumJupiter::class)
open class Spec {

  @DriverCapabilities
  val capabilities = DesiredCapabilities().apply {
    setAcceptInsecureCerts(true)
  }

  // private val keycloak = UserFixtureService()

  // protected fun withVerifiedUser(block: (user: User) -> Unit) {
  //   keycloak.createVerifiedUser().let {
  //     try {
  //       block(it)
  //     } finally {
  //       try {
  //         keycloak.deleteUser(it)
  //       } catch (e: UserFixtureException) {
  //         throw e
  //       }
  //     }
  //   }
  // }
}
