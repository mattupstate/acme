package com.acme.web.test.core

import org.openqa.selenium.By

fun buttonText(text: String): By? =
  By.xpath(".//button[contains(.,\"$text\")]")

fun formControl(name: String): By? =
  By.cssSelector("input[formControlName=$name")
