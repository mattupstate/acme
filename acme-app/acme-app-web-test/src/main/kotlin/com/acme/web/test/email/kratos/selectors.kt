package com.acme.web.test.email.kratos

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

fun WebElement.verificationLink() =
  findElement(By.xpath("/html/body/a"))
