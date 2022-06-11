package com.acme.web.test.data

import java.util.UUID

class UserFixtureService {
  class UserFixtureException(message: String) : RuntimeException(message)

  fun createVerifiedUser() =
    UUID.randomUUID().toString().let {
      createVerifiedUser("$it@acme.com", it, it)
    }

  fun createVerifiedUser(username: String, password: String, id: String) =

  fun deleteUser(user: User) {
    deleteUser(user.id)
  }

  fun deleteUser(user: String) {

  }
}
