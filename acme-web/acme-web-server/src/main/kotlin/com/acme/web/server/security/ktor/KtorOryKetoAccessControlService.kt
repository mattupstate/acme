package com.acme.web.server.security.ktor

import com.acme.web.server.security.OryKetoAccessControlService
import com.acme.web.server.security.keto.OryKetoClient
import com.acme.web.server.security.keto.ktor.KtorOryKetoClient
import com.acme.web.server.security.keto.ktor.defaultOryKetoReadClient
import com.acme.web.server.security.keto.ktor.defaultOryKetoWriteClient

class KtorOryKetoAccessControlService(namespace: String, client: OryKetoClient) :
  OryKetoAccessControlService(namespace, client) {

  constructor(namespace: String) : this(namespace, DEFAULT_CLIENT)

  companion object {
    val DEFAULT_CLIENT = KtorOryKetoClient(defaultOryKetoReadClient, defaultOryKetoWriteClient)
  }
}
