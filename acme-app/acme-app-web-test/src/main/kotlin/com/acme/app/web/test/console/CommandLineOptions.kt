package com.acme.app.web.test.console

/**
 * TODO: Expose options to command-line interface
 */
data class CommandLineOptions(
  val packageNames: List<String>,
  val classNamePatterns: List<String>,
) {
  companion object {
    val DEFAULT = CommandLineOptions(
      packageNames = listOf(
        "com.acme.app.web.test"
      ),
      classNamePatterns = listOf(
        ".*Spec",
        ".*Test",
        ".*Tests",
      )
    )
  }
}
