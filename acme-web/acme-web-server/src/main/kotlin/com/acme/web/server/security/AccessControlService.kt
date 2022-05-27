package com.acme.web.server.security

interface AccessControlService {
  suspend fun setResourceOwner(href: String, userId: String)
  suspend fun canReadResource(href: String, userId: String): Boolean
}
