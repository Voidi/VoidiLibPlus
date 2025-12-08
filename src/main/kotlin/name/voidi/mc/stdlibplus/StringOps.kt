package name.voidi.mc.stdlibplus

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.*
import java.util.stream.Stream

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
		return DataResult.success("$list$value\u001E")
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
				for(entry in it.toList()) {
					append("\u001E${entry!!.first}\u001F${entry.second}")
				}
				append("\u001E")
			}
		}
		return null
	}
	
	override fun getStream(input: String?): DataResult<Stream<String?>?>? {
		return DataResult.success(Stream.of(input))
	}
	
	override fun createList(input: Stream<String?>?): String? {
		return input?.let {
			it.toList().joinToString(prefix = "\u001E", separator = "\u001E", postfix = "\u001E")
		}
	}
	
	override fun remove(input: String?, key: String?): String? {
		return input
	}
}