package com.acme.scheduling

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class UserId(val value: String)
