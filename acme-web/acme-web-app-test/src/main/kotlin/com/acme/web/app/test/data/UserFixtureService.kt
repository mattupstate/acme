package com.acme.web.app.test.data

import java.util.UUID

interface UserFixtureService {
  suspend fun createVerifiedUser() =
    UUID.randomUUID().toString().let {
      createVerifiedUser("$it@acme.com", it, it)
    }

  suspend fun createVerifiedUser(username: String, password: String, id: String)
  suspend fun deleteUser(user: User) = deleteUser(user.id)
  suspend fun deleteUser(user: String)
}
