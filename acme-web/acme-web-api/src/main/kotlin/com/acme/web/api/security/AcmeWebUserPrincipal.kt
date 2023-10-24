package com.acme.web.api.security

interface AcmeWebUserPrincipal {
  val id: String
  val email: String
  val name: Name

  interface Name {
    val given: String
    val family: String
    val preferred: String
    val prefix: String
    val suffix: String
  }
}
