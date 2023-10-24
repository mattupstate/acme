package com.acme.web.app.logback

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.pattern.color.ANSIConstants.DEFAULT_FG
import ch.qos.logback.core.pattern.color.ANSIConstants.ESC_END
import ch.qos.logback.core.pattern.color.ANSIConstants.ESC_START
import ch.qos.logback.core.pattern.color.ANSIConstants.WHITE_FG

class ColoredExceptionConverter : ThrowableProxyConverter() {

  override fun throwableProxyToString(tp: IThrowableProxy?): String =
    super.throwableProxyToString(tp).let {
      StringBuilder().apply {
        it.lines().forEach {
          append(coloredLine(it))
          append(System.lineSeparator())
        }
      }.toString()
    }

  private fun coloredLine(line: String): String {
    PACKAGE_COLORS.entries.forEach {
      val start = line.indexOf(it.key)
      if (start >= 0)
        return colorLine(it.value, start, line)
    }
    return line
  }

  private fun colorLine(code: String, start: Int, line: String) =
    line.substring(0, start) + "$ESC_START$code$ESC_END${line.substring(start)}$ESC_START$DEFAULT_FG$ESC_END"

  companion object {
    val PACKAGE_COLORS = mapOf(
      "com.acme" to WHITE_FG
    )
  }
}
