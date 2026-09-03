package name.voidi.mc.voidilibplus.lifecycle.config

import com.mojang.serialization.Codec
import name.voidi.mc.voidilibplus.technical.StringOps
import net.neoforged.neoforge.common.ModConfigSpec
import kotlin.reflect.KProperty

class SectionDelegate<T>(val section: T) {
	operator fun getValue(thisRef: Any, property: KProperty<*>): T {
		return this.section
	}
}

class BooleanPropertyDelegate(val valueSpec: ModConfigSpec.BooleanValue) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
		return this.valueSpec.get()
	}
	
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
		this.valueSpec.set(value)
	}
}

class IntegerPropertyDelegate(val valueSpec: ModConfigSpec.IntValue) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
		return this.valueSpec.get()
	}
	
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
		this.valueSpec.set(value)
	}
}

class StringPropertyDelegate(val valueSpec: ModConfigSpec.ConfigValue<String>) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
		return this.valueSpec.get()
	}
	
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
		this.valueSpec.set(value)
	}
}

interface ListPropertyDelegate<T> {
	operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableList<T>
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableList<T>)
}

class ListNativeDelegate<T>(val valueSpec: ModConfigSpec.ConfigValue<MutableList<T>>) :
	ListPropertyDelegate<T> {
	override operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableList<T> {
		return this.valueSpec.get()
	}
	
	override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableList<T>) {
		this.valueSpec.set(value)
	}
}

open class ListSerializedDelegate<T : Any>(
	val valueSpec: ModConfigSpec.ConfigValue<MutableList<String>>,
	val codec: Codec<T>
) : ListPropertyDelegate<T> {
	var DeserializedCache: MutableList<T> = mutableListOf()
	
	protected fun encode(thing: T): String {
		return this.codec.encodeStart(StringOps.INSTANCE, thing).result().get()
	}
	
	protected fun decode(thing: String): T {
		return codec.parse(StringOps.INSTANCE, thing).result().get()
	}
	
	fun loadIntoCache() {
		this.DeserializedCache =
			this.valueSpec.get().map { stringRepresentation -> decode(stringRepresentation) }.toMutableList()
	}
	
	fun saveFromCache() {
		this.valueSpec.set(this.DeserializedCache.map { encode(it) }.toMutableList())
	}
	
	override operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableList<T> {
		return this.DeserializedCache
	}
	
	override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableList<T>) {
		this.DeserializedCache = value
	}
}
