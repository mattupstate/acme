package com.acme.core

@Suppress("UNCHECKED_CAST")
class MetaData {
  private val meta: MutableMap<String, Any> = mutableMapOf()

  fun <T> get(key: String) : T = meta[key] as T

  fun set(key: String, value: Any) {
    meta[key] = value
  }

  fun set(pair: Pair<String, Any>) {
    set(pair.first, pair.second)
  }

  fun set(vararg pairs: Pair<String, Any>) {
    pairs.forEach(::set)
  }
}
