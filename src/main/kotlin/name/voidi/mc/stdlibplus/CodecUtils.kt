package name.voidi.mc.stdlibplus

import com.mojang.serialization.*
import net.minecraft.core.*
import net.minecraft.resources.*

//val <T> T.SuitableCodec: Codec<T>?
//	get() {
//		val codec: Codec<T> = when (this) {
//			is ResourceLocation -> ResourceLocation.CODEC
//			is BlockPos -> BlockPos.CODEC
//			is GlobalPos -> GlobalPos.CODEC
//			else -> null
//		} as Codec<T>
//		return codec
//	}

@Suppress("UNCHECKED_CAST")
fun <T> defaultCodec(
	clazz: Class<T>
): Codec<T> {
	return when (clazz) {
		ResourceLocation::class.java -> ResourceLocation.CODEC
		BlockPos::class.java -> BlockPos.CODEC
		GlobalPos::class.java -> GlobalPos.CODEC
		else -> throw UnsupportedOperationException("There is no suitable codec for ${clazz.canonicalName}")
	} as Codec<T>
}

//inline fun <reified T: Any> T.defaultCodec(): Codec<T>? =
//    defaultCodec(T::class.java)

//fun <T> encode(thing: T): String {
//	val c = defaultCodec(thing!!.javaClass)
//		?: throw UnsupportedOperationException("There is no suitable codec for ${thing.javaClass.canonicalName}")
//
//	return c.encodeStart(StringOps.INSTANCE, thing).result().get()
//}
//
//inline fun <reified JavaType> decode(thing: String): JavaType {
//	val codec = defaultCodec<JavaType>(JavaType::class.java)
//		?: throw UnsupportedOperationException("There is no suitable codec for ${thing.javaClass.canonicalName}")
//
//	val o = codec.parse(StringOps.INSTANCE, thing).result().get()
//	return o as JavaType
//}
//
//fun <T : Any> decode(thing: String, codec: Codec<T>): T {
////	val codec = defaultCodec<JavaType>(JavaType::class.java)
////		?: throw UnsupportedOperationException("There is no suitable codec for ${thing.javaClass.canonicalName}")
//
//	val o = codec.parse(StringOps.INSTANCE, thing).result().get()
//	return o
//}


fun <K, V> pairListMap(keyCodec: Codec<K>, valueCodec: Codec<V>): Codec<MutableMap<K, V>> {
	return Codec.mapPair(keyCodec.fieldOf("key"), valueCodec.fieldOf("value"))
		.codec()
		.listOf()
		.xmap({ list ->
			return@xmap buildMap {
				for (entry in list) {
					if (this.containsValue(entry.second))
						put(entry.first, this.values.first { it == entry.second })
					else
						put(entry.first, entry.second)
				}
			}.toMutableMap()
		}, { map ->
			return@xmap buildList {
				for (entry in map.entries) {
					add(com.mojang.datafixers.util.Pair(entry.key, entry.value))
				}
			}
		})
}