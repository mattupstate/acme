package com.acme.web.app.security.keto

class KtorOryKetoAccessControlService(namespace: String, client: OryKetoClient) :
  OryKetoAccessControlService(namespace, client) {

  constructor(namespace: String) : this(namespace, DEFAULT_CLIENT)

  companion object {
    val DEFAULT_CLIENT = KtorOryKetoClient(defaultOryKetoReadClient, defaultOryKetoWriteClient)
  }
}
