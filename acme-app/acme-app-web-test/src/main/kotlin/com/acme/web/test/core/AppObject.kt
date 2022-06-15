package com.acme.web.test.core

import com.acme.web.test.app.SignInPage
import com.acme.web.test.data.User
import mu.KotlinLogging
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

val logger = KotlinLogging.logger {}

open class AppObject(
  internal val root: URL,
  internal val urlMap: Map<KClass<out Any>, String>,
  internal val driver: WebDriver
) {
  constructor(
    url: String,
    urlMap: Map<KClass<out Any>, String>,
    driver: WebDriver
  ) : this(URL(url), urlMap, driver)
}

fun <P : Page> AppObject.navigateAndMaybeSignIn(
  user: User,
  pageType: KClass<P>,
): P =
  try {
    navigate(pageType)
  } catch (e: NavigationException) {
    signIn(user)
    waitForPage(pageType, Duration.ofSeconds(5))
  }

fun AppObject.signIn(user: User) =
  waitForPage(SignInPage::class, Duration.ofSeconds(5))

fun <P : Page> AppObject.waitForPage(pageType: KClass<P>, timeout: Duration): P =
  pageType.primaryConstructor!!.call(driver).also {
    logger.debug("waiting for ${it.rootLocator}")
    WebDriverWait(driver, timeout).until(
      ExpectedConditions.presenceOfElementLocated(it.rootLocator)
    )
  }

fun <P : Page> AppObject.navigate(pageType: KClass<P>, timeout: Duration = Duration.ofSeconds(5)): P =
  driver.get(URL(root, urlMap[pageType]).toString()).run {
    try {
      waitForPage(pageType, timeout)
    } catch (e: WebDriverException) {
      throw NavigationException("Wait condition for ${pageType.simpleName} failed", e)
    }
  }

fun AppObject.waitFor(
  condition: ExpectedCondition<*>,
  duration: Duration = Duration.ofSeconds(5)
) {
  WebDriverWait(driver, duration).until(condition)
}

fun AppObject.waitForPresenceOfElement(
  selector: By,
  duration: Duration = Duration.ofSeconds(5)
): WebElement =
  WebDriverWait(driver, duration).until(
    ExpectedConditions.presenceOfElementLocated(selector)
  ).let {
    driver.findElement(selector)
  }

