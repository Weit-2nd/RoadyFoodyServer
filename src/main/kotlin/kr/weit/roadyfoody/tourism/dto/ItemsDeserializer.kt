package kr.weit.roadyfoody.tourism.dto

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

// Items 빈 문자열 처리
class ItemsDeserializer : JsonDeserializer<Items>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): Items {
        val node: JsonNode = p.codec.readTree(p)
        return if (node.isTextual && node.asText().isEmpty()) {
            Items(emptyList())
        } else {
            p.codec.treeToValue(node, Items::class.java)
        }
    }
}
