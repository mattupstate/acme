package com.acme.web.test.engine

import mu.KotlinLogging
import org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter
import kotlin.system.exitProcess

object EngineMain {
  private val logger = KotlinLogging.logger {}

  @JvmStatic
  fun main(args: Array<String>) {
    // TODO: Use a configuration library like https://github.com/lightbend/config
    val env: TestEnvironment =
      TestEnvironment.fromClassPathConfig()
    logger.info("{}", env)

    val request = LauncherDiscoveryRequestBuilder.request()
      .selectors(selectPackage(env.packageName))
      .filters(includeClassNamePatterns(*env.classNamePatterns.toTypedArray()))
      .build()

    val launcher = LauncherFactory.create()
    val listener = SummaryGeneratingListener()
    launcher.registerTestExecutionListeners(listener)
    launcher.execute(request)

    // TODO: Improve report output
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
