// package com.acme.web.test.specs
//
// import io.kotest.core.extensions.install
// import io.kotest.core.spec.style.ShouldSpec
// import io.kotest.extensions.testcontainers.TestContainerExtension
// import org.openqa.selenium.WebDriver
// import org.openqa.selenium.chrome.ChromeOptions
// import org.openqa.selenium.firefox.FirefoxOptions
// import org.testcontainers.containers.BrowserWebDriverContainer
//
// abstract class EndToEndSpec(body: ShouldSpec.(browsers: Collection<WebDriver>) -> Unit) : ShouldSpec({
//   val firefox = install(
//     TestContainerExtension(
//       BrowserWebDriverContainer().withCapabilities(FirefoxOptions())
//     )
//   )
//
//   val chrome = install(
//     TestContainerExtension(
//       BrowserWebDriverContainer().withCapabilities(ChromeOptions())
//     )
//   )
//
//   body(this, listOf(chrome.webDriver, firefox.webDriver))
// })
