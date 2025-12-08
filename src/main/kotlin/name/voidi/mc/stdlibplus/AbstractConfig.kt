package name.voidi.mc.stdlibplus

import com.mojang.serialization.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.*
import net.neoforged.fml.event.config.*
import net.neoforged.neoforge.common.*
import kotlin.reflect.*

object ObjectListCache {
	internal val listProperties = mutableListOf<ListSerializedDelegate<*>>()
	
	@SubscribeEvent
	fun onLoadForCache(event: ModConfigEvent.Loading) {
		MOD.LOGGER.debug("Caching deserilized Lists")
		for (property in listProperties) {
			property.loadIntoCache()
		}
	}
	
	@SubscribeEvent
	fun onReloadForCache(event: ModConfigEvent.Reloading) {
		MOD.LOGGER.debug("Caching deserilized Lists")
		for (property in listProperties) {
			property.loadIntoCache()
		}
	}
	
	@SubscribeEvent
	fun onUnloadCache(event: ModConfigEvent.Unloading) {
//		MOD.LOGGER.debug("Write cached Values into serilized Lists")
//		for (property in listProperties) {
//			property.saveFromCache()
//		}
	}
}

abstract class AbstractConfig(val MOD_ID: String, val builder: ModConfigSpec.Builder) {
	
	constructor(modid: String) : this(modid, ModConfigSpec.Builder())
	
	protected fun registerItemTag(id: String): TagKey<Item> {
		return TagKey.create<Item>(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(this.MOD_ID, id))
	}
	
	protected fun registerBlockTag(id: String): TagKey<Block> {
		return TagKey.create<Block>(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(this.MOD_ID, id))
	}
	
	protected fun <T> Section(comment: String? = null, sectionProvider: () -> T): SectionBuilder<T> {
		comment?.let { builder.comment(it) }
		return SectionBuilder<T>(sectionProvider)
	}
	
	protected fun BooleanProperty(builderBlock: BooleanPropertyBuilder.() -> Unit): BooleanPropertyBuilder {
		val builder = BooleanPropertyBuilder()
		builder.builderBlock()
		return builder
	}
	
	protected inline fun <reified T : Any> ListProperty(builderBlock: ListPropertyBuilder<T>.() -> Unit): ListPropertyBuilder<T> {
		val builder = ListPropertyBuilder<T>(T::class)
		builder.builderBlock()
		return builder
	}
	
	// region Builder
	
	inner class SectionBuilder<T>(val sectionProvider: () -> T) {
		operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): SectionDelegate<T> {
			this@AbstractConfig.builder.push(prop.name)
			val delegate = SectionDelegate<T>(sectionProvider())
			this@AbstractConfig.builder.pop()
			return delegate
		}
	}
	
	open inner class AbstractPropertyBuilder {
		var Comment: String? = null
		var GameRestart = false
		var WorldRestart = false
		
		fun genericThings(thisRef: Any, prop: KProperty<*>) {
			Comment?.let { builder.comment(it) }
			if (GameRestart)
				builder.gameRestart()
			if (WorldRestart)
				builder.worldRestart()
			builder.translation("configuration.${MOD_ID}.${builder.completeCurrentPath}${prop.name}")
		}
	}
	
	inner class BooleanPropertyBuilder() : AbstractPropertyBuilder() {
		var DefaultValue = true
		operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): BooleanPropertyDelegate {
			this.genericThings(thisRef, prop)
			return BooleanPropertyDelegate(builder.define(prop.name, DefaultValue))
		}
	}
	
	inner class ListPropertyBuilder<T : Any>(protected val clazz: KClass<T>) : AbstractPropertyBuilder() {
		var DefaultValue = { emptyList<T>() }
		var NewEntry: () -> T? = { DefaultValue().firstOrNull() }
		var ElementValidator: (T) -> Boolean = { true }
		operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): ListPropertyDelegate<T> {
			this.genericThings(thisRef, prop)
			if (
				this.clazz == Int::class
				|| this.clazz == Long::class
				|| this.clazz == Double::class
				|| this.clazz == Boolean::class
				|| this.clazz == String::class
			) {
				@Suppress("UNCHECKED_CAST")
				val configValue = builder.defineListAllowEmpty<T>(
					prop.name,
					this.DefaultValue,
					this.NewEntry,
					{ this.ElementValidator(it as T) }
				) as ModConfigSpec.ConfigValue<MutableList<T>>
				return ListNativeDelegate<T>(configValue )
			} else {
				val codec = defaultCodec(this.clazz.java)
				@Suppress("UNCHECKED_CAST")
				val configValue = builder.defineListAllowEmpty<String>(
					prop.name,
					{ this.DefaultValue().map { it.toString() } },
					{ this.NewEntry().toString() },
					{ this.ElementValidator(codec.parse(StringOps.INSTANCE,it as String).orThrow) }
				) as ModConfigSpec.ConfigValue<MutableList<String>>
				val delegate = ListSerializedDelegate<T>(configValue, codec)
				ObjectListCache.listProperties += delegate
				return delegate
			}
		}
	}
	
	// endregion
}

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

val ModConfigSpec.Builder.completeCurrentPath: String
	get() {
		val field = ModConfigSpec.Builder::class.java.getDeclaredField("currentPath")
		field.isAccessible = true
		val fieldType = field.type
		
		@Suppress("UNCHECKED_CAST")
		val path: List<String> = fieldType.cast(field.get(this)) as List<String>
		if (path.isEmpty())
			return ""
		return path.joinToString(postfix = ".")
	}