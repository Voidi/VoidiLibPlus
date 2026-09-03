package name.voidi.mc.voidilibplus.lifecycle.config

import name.voidi.mc.voidilibplus.technical.StringOps
import name.voidi.mc.voidilibplus.technical.defaultCodec
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.ModConfigSpec
import java.util.function.Predicate
import kotlin.reflect.*

abstract class AbstractConfig(val MOD_ID: String, val builder: ModConfigSpec.Builder) {
	
	constructor(modid: String) : this(modid, ModConfigSpec.Builder())
	
	protected fun registerItemTag(id: String): TagKey<Item> {
		return TagKey.create<Item>(Registries.ITEM, Identifier.fromNamespaceAndPath(this.MOD_ID, id))
	}
	
	protected fun registerBlockTag(id: String): TagKey<Block> {
		return TagKey.create<Block>(Registries.BLOCK, Identifier.fromNamespaceAndPath(this.MOD_ID, id))
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

	protected fun IntegerProperty(builderBlock: IntegerPropertyBuilder.() -> Unit): IntegerPropertyBuilder {
		val builder = IntegerPropertyBuilder()
		builder.builderBlock()
		return builder
	}

	protected fun StringProperty(builderBlock: StringPropertyBuilder.() -> Unit): StringPropertyBuilder {
		val builder = StringPropertyBuilder()
		builder.builderBlock()
		return builder
	}

	protected inline fun <reified T : Any> ListProperty(builderBlock: ListPropertyBuilder<T>.() -> Unit): ListPropertyBuilder<T> {
		val builder = ListPropertyBuilder<T>(T::class)
		builder.builderBlock()
		return builder
	}
	
	// region Builder
	/**
	 * The builder are delegate provider
	 */
	
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
	
	inner class IntegerPropertyBuilder() : AbstractPropertyBuilder() {
		var MaximumValue = Int.MAX_VALUE
		var MinimumValue = Int.MIN_VALUE
		var DefaultValue = 0
		operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): IntegerPropertyDelegate {
			this.genericThings(thisRef, prop)
			return IntegerPropertyDelegate(builder.defineInRange(prop.name, DefaultValue, MinimumValue, MaximumValue))
		}
	}
	
	inner class StringPropertyBuilder() : AbstractPropertyBuilder() {
		var DefaultValue: String = ""
		var ElementValidator: (String?) -> Boolean = { true }
		operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): StringPropertyDelegate {
			this.genericThings(thisRef, prop)
			return StringPropertyDelegate(builder.define<String>(prop.name, DefaultValue, Predicate<Any>{
				if(it == null)
					return@Predicate true
				this.ElementValidator(it as String)
			}))
		}
	}
	
	inner class ListPropertyBuilder<T : Any>(protected val clazz: KClass<T>) : AbstractPropertyBuilder() {
		var DefaultValue: () -> List<T> = { emptyList<T>() }
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
					{ this.NewEntry as T },
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
					{
						val data = codec.parse(StringOps.INSTANCE,it as String)
						try {
							this.ElementValidator(data.orThrow)
						} catch (e: IllegalStateException) {
							false
						}
					}
				) as ModConfigSpec.ConfigValue<MutableList<String>>
				val delegate = ListSerializedDelegate<T>(configValue, codec)
				ObjectListCache.listProperties += delegate
				return delegate
			}
		}
	}
	
	// endregion
}
