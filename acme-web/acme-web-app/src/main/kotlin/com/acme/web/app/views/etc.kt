package com.acme.web.app.views

import com.acme.web.app.config.KratosConfiguration
import com.acme.web.app.security.AcmeWebUserIdentity
import com.acme.web.app.security.PrincipalNotFoundException
import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.response.respond
import io.ktor.server.thymeleaf.ThymeleafContent
import java.util.Locale

fun ApplicationCall.authenticatedUser(): AcmeWebUserIdentity =
  authentication.principal() ?: throw PrincipalNotFoundException()

suspend fun ApplicationCall.respondTemplate(
  template: String,
  model: Map<String, Any> = emptyMap(),
  etag: String? = null,
  contentType: ContentType = ContentType.Text.Html.withCharset(Charsets.UTF_8),
  locale: Locale = Locale.getDefault(),
  fragments: Set<String> = emptySet()
): Unit = respond(
  ThymeleafContent(
    template,
    model.toMutableMap().apply {
      put("identity", authenticatedUser())
      put("kratos", KratosConfiguration.fromConfig(application.environment.config.config("kratos")))
    },
    etag,
    contentType,
    locale,
    fragments
  )
)

suspend fun ApplicationCall.respondTemplateFragment(
  name: String,
  fragment: String,
  model: Map<String, Any> = emptyMap(),
  etag: String? = null,
  contentType: ContentType = ContentType.Text.Html.withCharset(Charsets.UTF_8),
  locale: Locale = Locale.getDefault()
) {
  respondTemplate(name, model, etag, contentType, locale, setOf(fragment))
}
