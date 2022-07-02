package com.acme.selenium

import io.kotest.matchers.collections.shouldHaveSize
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

fun buttonText(text: String): By? =
  By.xpath(".//button[contains(.,\"$text\")]")

fun formControlByName(name: String): By? =
  By.cssSelector("input[formControlName=$name")

val WebDriver.wait get() = Wait(this)

inline val Int.windowsAreVisible: ExpectedCondition<Boolean>
  get() = ExpectedConditions.numberOfWindowsToBe(this)

fun WebDriver.switchToRemainingWindow() {
  windowHandles shouldHaveSize 1
  switchTo().window(windowHandles.first())
}

