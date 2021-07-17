package com.jcy.letsgohiking.util

import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

object GsonFactory {

    private val builder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateSerializer())
    private val gson = builder.create()

    fun get() = gson!!

    class DateSerializer : JsonSerializer<Date>, JsonDeserializer<Date> {
        private var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)

        override fun serialize(
            src: Date,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(format.format(src).toString())
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Date {
            val value = json.asString

            return try {
                format.let {
                    it.timeZone = TimeZone.getTimeZone("UTC")
                    val date: Date = it.parse(value)
                    it.timeZone = TimeZone.getDefault()
                    it.parse(it.format(date))
                }
            } catch (e: Exception) {
                Date()
            }
        }
    }
}