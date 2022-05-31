package com.acme.ktor.server.i18n

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.request.acceptLanguage
import io.ktor.util.AttributeKey
import java.util.Locale
import java.util.ResourceBundle

data class I18NContext(
  val currentLocale: Locale,
  val messagesBasename: String,
  val messageBundles: MutableMap<Locale, MessageBundle>
) {

  val messages get() = messagesFor(currentLocale)
  private fun messagesFor(locale: Locale) = messageBundles[locale] ?: MessageBundle(
    ResourceBundle.getBundle(messagesBasename, locale)
  ).also {
    messageBundles[locale] = it
  }
}

val i18PluginKey = AttributeKey<I18NContext>("i18nPluginKey")

val ApplicationCall.currentLocale: Locale
  get() = attributes[i18PluginKey].currentLocale

val ApplicationCall.messages: MessageBundle
  get() = attributes[i18PluginKey].messages

class MessageBundle(private val bundle: ResourceBundle) {
  operator fun get(name: String): String? = bundle.getString(name)
  fun getValue(name: String): String = this[name]!!
  fun getOrDefault(name: String, default: String): String = this[name] ?: default
  fun getOrDefault(name: String, lazyMessage: () -> Any): String = this[name] ?: lazyMessage().toString()
}

class I18NConfiguration {
  var baseName: String = "messages"
  var defaultLocale: Locale = Locale.US
}

val I18N = createApplicationPlugin(
  name = "I18N",
  createConfiguration = ::I18NConfiguration
) {
  val messageBundles: MutableMap<Locale, MessageBundle> = mutableMapOf()

  onCall { call ->
    val currentLocale = try {
      Locale(Locale.LanguageRange.parse(call.request.acceptLanguage()).first().range)
    } catch (e: RuntimeException) {
      pluginConfig.defaultLocale
    }
    call.attributes.put(i18PluginKey, I18NContext(currentLocale, pluginConfig.baseName, messageBundles))
  }
}
