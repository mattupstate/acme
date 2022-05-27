// package com.acme.web.test.data
//
// import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
// import org.keycloak.admin.client.KeycloakBuilder
// import org.keycloak.representations.idm.CredentialRepresentation
// import org.keycloak.representations.idm.UserRepresentation
// import java.util.UUID
// import javax.ws.rs.core.Response
// import kotlin.RuntimeException
//
// class UserFixtureException(message: String, val response: Response) : RuntimeException(message)
//
// class UserFixtureService {
//   private val client = KeycloakBuilder
//     .builder()
//     .serverUrl(System.getProperty("keycloak.admin.url"))
//     .realm(System.getProperty("keycloak.admin.realm") ?: "master")
//     .username(System.getProperty("keycloak.admin.username"))
//     .password(System.getProperty("keycloak.admin.password"))
//     .clientId(System.getProperty("keycloak.admin.client-id") ?: "admin-cli")
//     .resteasyClient(ResteasyClientBuilder().connectionPoolSize(10).disableTrustManager().build())
//     .build()
//
//   private val acmeRealm = client.realm(System.getProperty("keycloak.realm") ?: "acme")
//
//   fun createVerifiedUser(
//     username: String = "${UUID.randomUUID()}@acme.com",
//     password: String = UUID.randomUUID().toString(),
//     id: String = UUID.randomUUID().toString()
//   ) =
//     acmeRealm.users().create(
//       UserRepresentation().apply {
//         this.id = id
//         this.username = username
//         this.email = username
//         this.isEnabled = true
//         this.isEmailVerified = true
//         this.credentials = listOf(
//           CredentialRepresentation().apply {
//             this.type = CredentialRepresentation.PASSWORD
//             this.value = password
//           }
//         )
//       }
//     ).let {
//       if (it.status < 300) {
//         User(it.location.path.split("/").last(), username, password)
//       } else
//         throw UserFixtureException("Error creating user", it)
//     }
//
//   fun deleteUser(user: User) {
//     acmeRealm.users().delete(user.id).let {
//       if (it.status >= 400)
//         throw UserFixtureException("Error deleting user", it)
//     }
//   }
// }
