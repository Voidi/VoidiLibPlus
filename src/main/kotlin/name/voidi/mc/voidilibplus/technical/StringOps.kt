package name.voidi.mc.voidilibplus.technical

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import java.util.stream.Stream
import kotlin.collections.iterator

const val RECORD_SEPERATOR = '\u001E'
const val UNIT_SEPERATOR = '\u001F'
const val START_TEXT = '\u0002'
const val END_TEXT = '\u0003'
const val LIST_CONTROL = '\u0011'
const val MAP_CONTROL = '\u0012'

//TODO complete
class StringOps : DynamicOps<String> {
	
	companion object {
		val INSTANCE = StringOps()
	}
	override fun empty(): String {
		return ""
	}
	
	override fun <U : Any?> convertTo(outOps: DynamicOps<U?>?, input: String?): U? {
//		outOps?.let {ops ->
//			if (input instanceof JsonObject) {
//				return convertMap(ops, input);
//			}
//			if (input instanceof JsonArray) {
//				return convertList(ops, input);
//			}
//			if (input instanceof JsonNull) {
//				return ops.empty();
//			}
//			final JsonPrimitive primitive = input.getAsJsonPrimitive();
//			if (primitive.isString()) {
//				return ops.createString(primitive.getAsString());
//			}
//			if (primitive.isBoolean()) {
//				return ops.createBoolean(primitive.getAsBoolean());
//			}
//		}
		return null
	}
	
	override fun getBooleanValue(input: String?): DataResult<Boolean?>? {
		if (input == null)
			return null
		return when (input) {
			"true" -> DataResult.success(true)
			"false" -> DataResult.success(false)
			else -> DataResult.error { "Invalid boolean value: $input" }
		}
	}

	override fun createBoolean(value: Boolean): String? {
		return if (value) "true" else "false"
	}

	override fun getNumberValue(input: String?): DataResult<Number?>? {
		if (input == null)
			return null
		return try {
			DataResult.success(input.toDouble())
		} catch (e: NumberFormatException) {
			DataResult.success(input.toInt())
		}
	}
	
	override fun createNumeric(i: Number?): String? {
		return i.toString()
	}
	
	override fun getStringValue(input: String?): DataResult<String?>? {
		return DataResult.success<String>(input)
	}
	
	override fun createString(value: String?): String? {
		return value
	}
	
	override fun mergeToList(list: String?, value: String?): DataResult<String?>? {
		return DataResult.success("$START_TEXT$list$RECORD_SEPERATOR$value$END_TEXT")
	}
	
	override fun mergeToMap(
		map: String?,
		key: String?,
		value: String?
	): DataResult<String?>? {
		TODO("Not yet implemented")
	}
	
	override fun getMapValues(input: String?): DataResult<Stream<Pair<String?, String?>?>?>? {
		TODO("Not yet implemented")
	}
	
	override fun createMap(map: Stream<Pair<String?, String?>?>?): String? {
		map?.let {
			return buildString {
				append(MAP_CONTROL)
				append(START_TEXT)
				for(entry in it.toList()) {
					append("${entry!!.first}$UNIT_SEPERATOR${entry.second}$RECORD_SEPERATOR")
				}
				
			}.dropLast(1) + END_TEXT
		}
		return null
	}
	
	override fun getStream(input: String?): DataResult<Stream<String?>?>? {
		return DataResult.success(Stream.of(input))
	}
	
	override fun createList(input: Stream<String?>?): String? {
		return "$LIST_CONTROL$START_TEXT" + input?.toList()?.joinToString(separator = RECORD_SEPERATOR.toString()) + END_TEXT
	}
	
	override fun remove(input: String?, key: String?): String? {
		if(input == null)
			return null
		if(input[0] == MAP_CONTROL) {
			val map = input.drop(1).dropLast(1).split(RECORD_SEPERATOR).associate<String, String, String> { entry: String ->
				entry.split(UNIT_SEPERATOR).let { kotlin.Pair(it[0], it[1]) }
			}.filterKeys { it != key }
			return buildString {
				append(MAP_CONTROL)
				append(START_TEXT)
				for((key, value) in map) {
					append("$key$UNIT_SEPERATOR$value$RECORD_SEPERATOR")
				}
				
			}.dropLast(1) + END_TEXT		}
		return input
	}
}