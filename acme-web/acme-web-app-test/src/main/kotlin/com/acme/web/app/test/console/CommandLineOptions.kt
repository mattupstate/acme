package com.acme.web.app.test.console

/**
 * TODO: Expose options to command-line interface
 */
data class CommandLineOptions(
  val packageNames: List<String>,
  val classNamePatterns: List<String>,
) {
  companion object {
    val DEFAULT = com.acme.web.app.test.console.CommandLineOptions(
      packageNames = listOf(
        "com.acme.web.app.test"
      ),
      classNamePatterns = listOf(
        ".*Spec",
        ".*Test",
        ".*Tests",
      )
    )
  }
}
