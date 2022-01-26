package wikibook.learnandroid.jsondeserialization
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class MyJSONDataClass(val data1: Int, val data2: String, val list: List<Int>)

data class MyJSONNestedDataClass(val nested: Map<String, Any>)

data class JSONData(val nested: JSONNested)

data class JSONNested(val data1: Int, val data2: String, val list: List<Int>)

data class ComplexJSONData(val nested: ComplexJSONNested)

data class ComplexJSONNested(@JsonProperty("inner_data") val innerData: String, @JsonProperty("inner_nested") val innerNested: ComplexJSONInnerNested)

data class ComplexJSONInnerNested(val data1: Int, val data2: String, val list: List<Int>)

@JsonDeserialize(using = MyComplexJSONDataDeserializer::class)
data class ComplexJSONData2(val innerData: String?, val data1: Int?, val data2: String?, val list: List<Int>?)

class MyComplexJSONDataDeserializer : StdDeserializer<ComplexJSONData2>(ComplexJSONData2::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ComplexJSONData2 {
        val node : JsonNode? = p?.codec?.readTree<JsonNode>(p)

        val nestedNode: JsonNode? = node?.get("nested")
        val innerDataValue = nestedNode?.get("inner_data")?.asText()
        val innerNestedNode = nestedNode?.get("inner_nested")
        val innerNestedData1Node = innerNestedNode?.get("data1")?.asInt()
        val innerNestedData2Node = innerNestedNode?.get("data2")?.asText()

        val list = mutableListOf<Int>()
        innerNestedNode?.get("list")?.elements()?.forEach { list.add(it.asInt()) }

        return ComplexJSONData2(innerDataValue, innerNestedData1Node, innerNestedData2Node, list)
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapper = jacksonObjectMapper()

        val jsonString = """{"data1": 1234, "data2": "Hello", "list": [1,2,3]}"""
        var d1 = mapper?.readValue<MyJSONDataClass>(jsonString)

        Log.d("mytag", "${d1.data1}")
        Log.d("mytag", "${d1.data2}")
        Log.d("mytag", "${d1.list}")

        val jsonString2 = """{"nested" : {"data1": 1234, "data2": "Hello", "list": [1,2,3]}}"""
        var d2 = mapper?.readValue<MyJSONNestedDataClass>(jsonString2)

        Log.d("mytag", "${d2.nested["data1"]}")
        Log.d("mytag", "${d2.nested["data2"]}")
        Log.d("mytag", "${d2.nested["list"]}")

        val d3 = mapper?.readValue<JSONData>(jsonString2)

        Log.d("mytag", "${d3.nested.data1}")
        Log.d("mytag", "${d3.nested.data2}")
        Log.d("mytag", "${d3.nested.list}")

        val complexJsonString = """{"nested": {"inner_data": "Hello from inner", "inner_nested" : { "data1": 1234, "data2": "Hello", "list": [1,2,3] } } }"""
        var d4 = mapper?.readValue<ComplexJSONData>(complexJsonString)

        Log.d("mytag", "${d4.nested.innerData}")
        Log.d("mytag", "${d4.nested.innerNested.data1}")
        Log.d("mytag", "${d4.nested.innerNested.data2}")
        Log.d("mytag", "${d4.nested.innerNested.list}")

        var d5 = ObjectMapper().readValue<ComplexJSONData2>(complexJsonString)
        Log.d("mytag", "${d5.innerData}")
        Log.d("mytag", "${d5.data1}")
        Log.d("mytag", "${d5.data2}")
        Log.d("mytag", "${d5.list}")
    }
}