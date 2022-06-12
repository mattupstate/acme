package com.acme.web.test.data

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture

val fixture = kotlinFixture {
  javaFakerStrategy {
    putProperty("password") {
      internet().password(
        12, 24, true, true, true
      )
    }
  }
}
