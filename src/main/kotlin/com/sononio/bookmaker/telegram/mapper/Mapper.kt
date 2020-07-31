package com.sononio.bookmaker.telegram.mapper

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

@Suppress("FunctionName")
inline fun <reified T : Any> Mapper() = Mapper(T::class)

interface MapperFields {
    fun editFields() : List<KProperty<*>>
    fun createFields() : List<KProperty<*>>
}

open class Mapper<T : Any>(private val type: KClass<T>) {
    protected var basedOn: T? = null

    protected class BuilderField(val value: Any?, val type: KType)
    private val fields: MutableMap<String, BuilderField> = LinkedHashMap()

    fun <V> with(property: KProperty<V>, value: Any?) : Mapper<T> {
        fields[property.name] = BuilderField(value, property.returnType)
        return this
    }

    fun <I : T?> withBasedOn(obj: I) : Mapper<T> {
        basedOn = obj
        return this
    }

    fun <V> getField(property: KProperty<V>) : Any? = fields[property.name]?.value

    fun map(): T {
        val obj = basedOn ?: createObject()

        for ((name, field) in fields) {
            val property = type.memberProperties.find { it.name == name }!! as KMutableProperty1
            property.setter.call(obj, field.value)
        }

        return obj
    }

    private fun createObject(): T {
        val constructor = type.primaryConstructor!!
        val params = constructor.parameters.map { fields[it.name]?.value }.toTypedArray()
        return constructor.call(*params)
    }
}