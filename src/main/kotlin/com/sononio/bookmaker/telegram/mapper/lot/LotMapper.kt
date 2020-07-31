package com.sononio.bookmaker.telegram.mapper.lot

import com.sononio.bookmaker.model.lot.IntLot
import com.sononio.bookmaker.model.lot.Lot
import com.sononio.bookmaker.model.lot.NumericLot
import com.sononio.bookmaker.model.lot.PercentLot
import com.sononio.bookmaker.telegram.mapper.Mapper
import com.sononio.bookmaker.telegram.mapper.MapperFields
import com.sononio.bookmaker.util.enhancement.toDate
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

enum class LotTypeMessage {
    INT,
    PERCENT;

    fun mapper(): LotMapper<out Lot> = when (this) {
        INT -> IntLotMapper()
        PERCENT -> PercentLotMapper()
    }
}

class TypeParser<T : Any> private constructor(val kClass: KClass<T>, private val parseImpl : TypeParser<T>.(String) -> T) {
    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        fun <V : Any> valueOf(type: KClass<V>) : TypeParser<V> {
            return when (type) {
                Integer::class -> TypeParser(type) { Integer.parseInt(it) as V }
                BigDecimal::class -> TypeParser(type) { BigDecimal(it) as V }
                Date::class -> TypeParser(type) { it.toDate(DATE_FORMATTER) as V }
                String::class -> TypeParser(type) { it as V }
                else -> throw IllegalArgumentException("No parser for type $type")
            }
        }
    }

    fun parse(from: String?) : Pair<T?, Boolean> {
        if (from == "-") return Pair(null, false)
        if (from == null || from == "" || from == ".") return Pair(null, true)
        return Pair(this.parseImpl(from), false)
    }
}

sealed class LotMapper<T : Lot>(type: KClass<T>) : Mapper<T>(type), MapperFields {
    fun mapStrings(lines : List<String>) : LotMapper<T> {
        val fields = when(basedOn) {
            null -> createFields()
            else -> editFields()
        }

        if (fields.size < lines.size)
            throw IllegalArgumentException("Too much lines (${lines.size}) for fields $fields")

        for ((i, line) in lines.withIndex()) {
            val (parsed, skip) = TypeParser.valueOf(fields[i].returnType.jvmErasure).parse(line)

            // While entity is exists we can skip some values if they didn't changed.
            // But we have to map even skipped fields to new entity as nulls
            if (!skip || basedOn == null) with(fields[i], parsed)
        }

        return this
    }
}
abstract class NumericLotMapper<T : NumericLot>(type: KClass<T>) : LotMapper<T>(type) {
    override fun editFields(): List<KProperty<*>> = createFields()
    override fun createFields(): List<KProperty<*>> = listOf(
            NumericLot::name,
            NumericLot::description,
            NumericLot::question,
            NumericLot::allowedError,
            NumericLot::endBetsTime,
            NumericLot::resultsTime)
}

class IntLotMapper : NumericLotMapper<IntLot>(IntLot::class)
class PercentLotMapper : NumericLotMapper<PercentLot>(PercentLot::class)