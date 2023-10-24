package com.acme.web.app.security.keto

import com.acme.web.app.security.AccessControlService

open class OryKetoAccessControlService(
  private val namespace: String,
  private val client: OryKetoClient,
) : AccessControlService {

  override suspend fun setResourceOwner(href: String, userId: String) {
    client.create(RelationTuple(namespace, href, REL_OWNER, userId))
  }

  override suspend fun canReadResource(href: String, userId: String): Boolean =
    client.check(RelationTuple(namespace, href, REL_OWNER, userId)).allowed

  companion object {
    const val REL_OWNER = "owner"
  }
}
