package māia.topology.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import māia.configure.json.JSONConfigurationReader
import māia.topology.visitation.TopologyVisitable
import māia.util.classForName
import māia.util.map


/**
 * Reads a topology from a JSON document.
 *
 * @param json  The source JSON object to read the topology from.
 */
class JSONTopologyReader(private val json : JsonObject) : TopologyVisitable {

    /**
     * Reads a topology from a JSON document.
     *
     * @param json  The source JSON string to read the topology from.
     */
    constructor(json : String) : this(Json.parseToJsonElement(json) as JsonObject)

    override fun iterateNodes() : Iterator<TopologyVisitable.Node> = (json["nodes"] as JsonArray).iterator().map {
        val nodeObject = it as JsonObject
        TopologyVisitable.Node(
                classForName((nodeObject["type"] as JsonPrimitive).content),
                JSONConfigurationReader((nodeObject["configuration"] as JsonObject))
        )
    }

    override fun iterateSubscriptions() : Iterator<TopologyVisitable.Subscription> = (json["subscriptions"] as JsonArray).iterator().map {
        val subscriptionObject = it as JsonObject
        TopologyVisitable.Subscription(
                (subscriptionObject["outputNode"] as JsonPrimitive).int,
                (subscriptionObject["outputName"] as JsonPrimitive).content,
                (subscriptionObject["inputNode"] as JsonPrimitive).int,
                (subscriptionObject["inputName"] as JsonPrimitive).content
        )
    }
}
