package com.koenv.fsxchecklists.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.koenv.fsxchecklists.model.ModelIndex
import java.lang.reflect.Type

class ModelIndexDeserializer : JsonDeserializer<ModelIndex> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ModelIndex {
        return ModelIndex(json.asJsonObject.get("name").asString, json.asJsonObject.get("headerImage").asString, json.asJsonObject.get("file").asString)
    }
}