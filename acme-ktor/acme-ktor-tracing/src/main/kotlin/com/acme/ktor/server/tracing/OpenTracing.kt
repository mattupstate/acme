package com.acme.ktor.server.tracing

import io.ktor.http.Headers
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.CallSetup
import io.ktor.server.application.hooks.ResponseSent
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.util.AttributeKey
import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.propagation.Format
import io.opentracing.propagation.TextMapAdapter
import io.opentracing.tag.Tags
import io.jaegertracing.Configuration as JaegerConfiguration

private val tracerSpanKey = AttributeKey<Span>("tracerSpanKey")

val ApplicationCall.span: Span get() = attributes[tracerSpanKey]

// private fun getDefaultTracer(pipeline: Application, config: OpenTracingConfiguration) =
//   JaegerConfiguration.fromEnv(
//     config.serviceName ?: pipeline.environment.config.property("ktor.application.id").getString()
//   ).tracer

// class HeadersBuilderCarrier(private val builder: HeadersBuilder) : TextMap {
//
//   override fun put(key: String, value: String) {
//     builder.append(key, value)
//   }
//
//   override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, String>> {
//     throw UnsupportedOperationException("Carrier is write-only")
//   }
// }

class OpenTracingConfiguration {
  var serviceName: String? = null
  var tracer: Tracer? = null
  val ignoredPaths = mutableSetOf<String>()
  var withSpan: ApplicationCall.(builder: Tracer.SpanBuilder) -> Unit = { builder ->
    listOf(
      Tags.SPAN_KIND.key to Tags.SPAN_KIND_SERVER,
      Tags.HTTP_URL.key to request.path(),
      Tags.HTTP_METHOD.key to request.httpMethod.value
    ).forEach {
      builder.withTag(it.first, it.second)
    }
  }

  fun ignore(vararg paths: String) = ignoredPaths.addAll(paths)
  fun ignore(paths: List<String>) = ignore(*paths.toTypedArray())
}

val OpenTracing = createApplicationPlugin(
  name = "OpenTracing",
  createConfiguration = ::OpenTracingConfiguration
) {
  val tracer = pluginConfig.tracer ?: JaegerConfiguration.fromEnv(pluginConfig.serviceName).tracer

  fun buildSpan() =
    tracer.buildSpan(ApplicationCallPipeline::class.simpleName)

  fun extractContext(headers: Headers): SpanContext? =
    tracer.extract(
      Format.Builtin.HTTP_HEADERS,
      TextMapAdapter(
        headers.entries().associate {
          it.key to it.value.first()
        }
      )
    )

  fun extractOrCreateContext(call: ApplicationCall) =
    extractContext(call.request.headers)?.let {
      buildSpan().asChildOf(it)
    } ?: run {
      buildSpan()
    }

  fun startSpan(call: ApplicationCall) {
    if (!pluginConfig.ignoredPaths.contains(call.request.path())) {
      extractOrCreateContext(call).apply {
        pluginConfig.withSpan(call, this)
      }.also {
        call.attributes.put(tracerSpanKey, it.start())
      }
    }
  }

  fun finishSpan(call: ApplicationCall) {
    call.attributes.getOrNull(tracerSpanKey)?.also {
      it.finish()
    }
  }

  on(CallSetup) {
    startSpan(it)
  }

  on(ResponseSent) {
    finishSpan(it)
  }
}

// class OpenTracing(
//   val tracer: Tracer,
//   val withSpan: ApplicationCall.(builder: Tracer.SpanBuilder) -> Unit,
//   private val ignoredPaths: Set<String>
// ) {
//
//   private fun buildSpan() =
//     tracer.buildSpan(ApplicationCallPipeline::class.simpleName)
//
//   private fun extractContext(headers: Headers): SpanContext? =
//     tracer.extract(
//       Format.Builtin.HTTP_HEADERS,
//       TextMapAdapter(
//         headers.entries().associate {
//           it.key to it.value.first()
//         }
//       )
//     )
//
//   private fun extractOrCreateContext(call: ApplicationCall) =
//     extractContext(call.request.headers)?.let {
//       buildSpan().asChildOf(it)
//     } ?: run {
//       buildSpan()
//     }
//
//   fun startSpan(call: ApplicationCall) {
//     if (!ignoredPaths.contains(call.request.path())) {
//       extractOrCreateContext(call).apply {
//         withSpan(call, this)
//       }.also {
//         call.attributes.put(TracingSpanKey, it.start())
//       }
//     }
//   }
//
//   fun finishSpan(call: ApplicationCall) {
//     call.attributes.getOrNull(TracingSpanKey)?.also {
//       it.finish()
//     }
//   }
//
//   class Configuration {
//     var serviceName: String? = null
//     var tracer: Tracer? = null
//     val ignoredPaths = mutableSetOf<String>()
//     var withSpan: ApplicationCall.(builder: Tracer.SpanBuilder) -> Unit = { builder ->
//       listOf(
//         Tags.SPAN_KIND.key to Tags.SPAN_KIND_SERVER,
//         Tags.HTTP_URL.key to request.path(),
//         Tags.HTTP_METHOD.key to request.httpMethod.value
//       ).forEach {
//         builder.withTag(it.first, it.second)
//       }
//     }
//
//     fun ignore(vararg paths: String) = ignoredPaths.addAll(paths)
//     fun ignore(paths: List<String>) = ignore(*paths.toTypedArray())
//   }
//
//   companion object Feature : ApplicationFeature<Application, Configuration, OpenTracing> {
//     override val key = AttributeKey<OpenTracing>("OpenTracing")
//
//     private fun getDefaultTracer(pipeline: Application, config: Configuration) =
//       JaegerConfiguration.fromEnv(
//         config.serviceName ?: pipeline.environment.config.property("ktor.application.id").getString()
//       ).tracer
//
//     override fun install(pipeline: Application, configure: Configuration.() -> Unit): OpenTracing {
//       val configuration = Configuration().apply(configure)
//
//       val feature = OpenTracing(
//         configuration.tracer ?: getDefaultTracer(pipeline, configuration),
//         configuration.withSpan,
//         configuration.ignoredPaths
//       )
//
//       pipeline.intercept(ApplicationCallPipeline.Setup) {
//         feature.startSpan(call)
//       }
//
//       pipeline.sendPipeline.intercept(ApplicationSendPipeline.After) {
//         feature.finishSpan(call)
//       }
//
//       return feature
//     }
//   }
// }
