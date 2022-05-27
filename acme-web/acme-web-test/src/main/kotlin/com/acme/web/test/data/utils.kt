package com.acme.web.test.data

fun randomLowercaseLetters(length: Int): String {
  val chars = ('a'..'z')
  return (1..length)
    .map { chars.random() }
    .joinToString("")
}


fun randomLettersAndNumbers(length: Int): String {
  val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
  return (1..length)
    .map { chars.random() }
    .joinToString("")
}

fun randomLetters(length: Int): String {
  val chars = ('A'..'Z') + ('a'..'z')
  return (1..length)
    .map { chars.random() }
    .joinToString("")
}

fun randomEmailAddress(domain: String) =
  "${randomLowercaseLetters(5)}@$domain"

fun randomEmailAddress() = randomEmailAddress("${randomLowercaseLetters(5)}.com")

// fun randomNPI(): String {
//   fun add(a: Int, b: Int) = a + b
//   val doubled = Array(5) { nextInt(5) }
//   val standard = Array(4) { nextInt(10) }
//   val check = (10 - ((doubled.map { e -> e * 2 }.reduce(::add) + standard.reduce(::add) + 4) % 10) % 10)
//   return "${doubled[0]}${standard[0]}${doubled[1]}${standard[1]}${doubled[2]}${standard[2]}${doubled[3]}${standard[3]}${doubled[4]}$check"
// }
