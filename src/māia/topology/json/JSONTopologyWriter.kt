package māia.topology.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import māia.configure.json.JSONConfigurationWriter
import māia.configure.visitation.ConfigurationVisitable
import māia.configure.visitation.visit
import māia.topology.Node
import māia.topology.visitation.TopologyVisitor
import kotlin.reflect.KClass


/**
 * Writes a topology to JSON.
 */
class JSONTopologyWriter : TopologyVisitor {

    private lateinit var nodes : ArrayList<JsonObject>
    private lateinit var subscriptions : ArrayList<JsonObject>
    private var result : JsonObject? = null

    private val configurationWriter = JSONConfigurationWriter()

    override fun begin() {
        nodes = ArrayList()
        subscriptions = ArrayList()
        result = null
    }

    override fun node(type : KClass<out Node<*>>, configuration : ConfigurationVisitable) {
        val node = HashMap<String, JsonElement>()
        node["type"] = JsonPrimitive(type.qualifiedName)
        node["configuration"] = configurationWriter.visit(configuration).toJson()
        nodes.add(JsonObject(node))
    }

    override fun subscription(fromNode : Int,
                              fromNodeOutputName : String,
                              toNode : Int,
                              toNodeInputName : String) {
        val subscription = HashMap<String, JsonPrimitive>()
        subscription["outputNode"] = JsonPrimitive(fromNode)
        subscription["outputName"] = JsonPrimitive(fromNodeOutputName)
        subscription["inputNode"] = JsonPrimitive(toNode)
        subscription["inputName"] = JsonPrimitive(toNodeInputName)
        subscriptions.add(JsonObject(subscription))
    }

    override fun end() {
        val json = HashMap<String, JsonArray>()
        json["nodes"] = JsonArray(nodes)
        json["subscriptions"] = JsonArray(subscriptions)
        result = JsonObject(json)
    }

    fun toJson() : JsonObject {
        return result ?: throw Exception("Writer not ended")
    }

    override fun toString() : String {
        return toJson().toString()
    }
}
