@file:OptIn(ExperimentalSerializationApi::class)

package com.acme.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URI

@Serializer(URI::class)
class URIAsStringSerializer : KSerializer<URI> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): URI =
    URI(decoder.decodeString())

  override fun serialize(encoder: Encoder, value: URI) {
    encoder.encodeString(value.toString())
  }
}
