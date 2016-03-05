package com.koenv.fsxchecklists.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.koenv.fsxchecklists.model.ManufacturerIndex
import com.koenv.fsxchecklists.model.ModelIndex
import com.koenv.fsxchecklists.util.typeToken
import java.lang.reflect.Type

class ManufacturerIndexDeserializer : JsonDeserializer<ManufacturerIndex> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ManufacturerIndex {
        return ManufacturerIndex(json.asJsonObject.get("name").asString, context.deserialize(json.asJsonObject.get("models"), typeToken<List<ModelIndex>>()))
    }
}