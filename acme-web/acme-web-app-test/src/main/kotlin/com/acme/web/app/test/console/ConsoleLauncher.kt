package com.acme.web.app.test.console

import org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.launcher.core.LauncherConfig
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter
import kotlin.system.exitProcess

object ConsoleLauncher {

  @JvmStatic
  fun main(args: Array<String>) {
    val options = com.acme.web.app.test.console.CommandLineOptions.Companion.DEFAULT

    val launcherConfig: LauncherConfig = LauncherConfig.builder()
      .enableTestEngineAutoRegistration(true)
      .build()

    val request = LauncherDiscoveryRequestBuilder.request()
      .selectors(
        options.packageNames.map(::selectPackage)
      )
      .filters(
        includeClassNamePatterns(*options.classNamePatterns.toTypedArray())
      )
      .build()

    val listener = SummaryGeneratingListener()

    LauncherFactory.openSession(launcherConfig).use {
      it.launcher.execute(request, listener)
    }

    val summary = listener.summary
    val write = PrintWriter(System.out)

    if (summary.totalFailureCount > 0) {
      summary.printFailuresTo(write)
      summary.printTo(write)
      exitProcess(1)
    } else {
      summary.printTo(write)
      exitProcess(0)
    }
  }
}
