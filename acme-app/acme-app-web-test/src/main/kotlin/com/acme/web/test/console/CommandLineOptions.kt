package com.acme.web.test.console

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
        "com.acme.web.test.specs"
      ),
      classNamePatterns = listOf(
        ".*Spec",
      )
    )
  }
}
