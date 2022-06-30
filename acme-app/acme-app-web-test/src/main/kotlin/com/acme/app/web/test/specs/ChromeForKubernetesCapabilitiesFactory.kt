package com.acme.app.web.test.specs

import org.fluentlenium.configuration.CapabilitiesFactory
import org.fluentlenium.configuration.ConfigurationProperties
import org.fluentlenium.configuration.FactoryName
import org.openqa.selenium.Capabilities
import org.openqa.selenium.chrome.ChromeOptions

@FactoryName("chrome-for-kubernetes")
class ChromeForKubernetesCapabilitiesFactory : CapabilitiesFactory {
  override fun newCapabilities(configuration: ConfigurationProperties?): Capabilities =
    ChromeOptions().apply {
      setAcceptInsecureCerts(true)
      setCapability("se:cdpEnabled", "false")
      setExperimentalOption(
        "localState", mapOf(
          "dns_over_https.mode" to "off",
          "dns_over_https.templates" to ""
        )
      )
    }
}
