package com.acme.web.api.security

import com.acme.web.api.security.keto.KtorOryKetoClient
import com.acme.web.api.security.keto.OryKetoClient
import com.acme.web.api.security.keto.defaultOryKetoReadClient
import com.acme.web.api.security.keto.defaultOryKetoWriteClient

class KtorOryKetoAccessControlService(namespace: String, client: OryKetoClient) :
  OryKetoAccessControlService(namespace, client) {

  constructor(namespace: String) : this(namespace, DEFAULT_CLIENT)

  companion object {
    val DEFAULT_CLIENT = KtorOryKetoClient(defaultOryKetoReadClient, defaultOryKetoWriteClient)
  }
}
