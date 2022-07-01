package com.acme.app.web.test.console

import org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter
import kotlin.system.exitProcess

object ConsoleLauncher {

  @JvmStatic
  fun main(args: Array<String>) {
    val options = CommandLineOptions.DEFAULT

    val request = LauncherDiscoveryRequestBuilder.request()
      .selectors(
        options.packageNames.map(::selectPackage)
      )
      .filters(
        includeClassNamePatterns(*options.classNamePatterns.toTypedArray())
      )
      .build()

    val launcher = LauncherFactory.create()
    val listener = SummaryGeneratingListener()

    launcher.execute(request, listener)

    val summary = listener.summary
    val write = PrintWriter(System.err)

    if (summary.totalFailureCount > 0) {
      summary.printFailuresTo(write)
      exitProcess(1)
    } else {
      summary.printTo(write)
    }
  }
}
