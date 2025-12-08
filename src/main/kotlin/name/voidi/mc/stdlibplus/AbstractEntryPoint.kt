package name.voidi.mc.stdlibplus

import net.minecraft.core.registries.*
import net.minecraft.resources.*
import net.minecraft.tags.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*
import net.neoforged.api.distmarker.*
import net.neoforged.bus.api.*
import net.neoforged.fml.*
import net.neoforged.fml.config.*
import net.neoforged.neoforge.client.gui.*
import net.neoforged.neoforge.registries.*
import org.slf4j.*
import thedarkcolour.kotlinforforge.neoforge.forge.*

abstract class AbstractEntryPoint(
	override val MOD_ID: String, override val modEventBus: IEventBus, override val modContainer: ModContainer, dist: Dist
) : ModEntryPoint {
	
	// Directly reference a slf4j logger
	override val LOGGER = LoggerFactory.getLogger(MOD_ID)
	
//	protected val DataGen_Factories: MutableList<DataGenFactory> = mutableListOf()

	val Registry_DataComponents: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID)
	
	// Create a Deferred Register to hold Items which will all be registered under the "modid" namespace
	val Registry_Items: DeferredRegister.Items = DeferredRegister.createItems(MOD_ID)
	
	init {
		
		modEventBus.addListener(::onCommonSetup)
		modEventBus.addListener(::onDateGenClient)
		
		Registry_DataComponents.register(modEventBus)
		Registry_Items.register(modEventBus)
		
		//Cache loading storing methods for serialized lists
		modEventBus.register(ObjectListCache)
		runWhenOn(Dist.CLIENT) {
			// This will use NeoForge's ConfigurationScreen to display this mod's configs
			this.modContainer.registerExtensionPoint(
				IConfigScreenFactory::class.java,
				IConfigScreenFactory { container, screen -> ConfigurationScreen(container, screen)}
			)
		}
	}
	
//	override fun onDateGenClient(event: GatherDataEvent.Client) {
//		for (factory in DataGen_Factories) {
//			event.createProvider { output, lookupProvider ->
//				AdvancementProvider(output, lookupProvider, listOf(GenericAdvancementsProvider(this.MOD_ID, factory)))
//			}
//		}
//	}
//
//	fun registerDataGen(factory: DataGenFactory) {
//		this.DataGen_Factories += factory
//	}
	
	fun <T : AbstractConfig> registerConfig(type: ModConfig.Type, config: T): T {
		modContainer.registerConfig(type, config.builder.build())
		return config
	}
	
	protected fun registerItemTag(id: String): TagKey<Item> {
		return TagKey.create<Item>(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(this.MOD_ID, id))
	}
	
	protected fun registerBlockTag(id: String): TagKey<Block> {
		return TagKey.create<Block>(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(this.MOD_ID, id))
	}
	
	fun debug(marker: Marker, vararg message: Any?) {
		this.LOGGER.debug(marker, message.joinToString(separator = ";"))
	}
	
	fun debug(vararg message: Any?) {
		this.LOGGER.debug(message.joinToString(separator = ";"))
	}
	
	
	fun info(marker: Marker? = null, message: Any?) {
		if (marker == null) this.LOGGER.info(message.toString())
		else this.LOGGER.info(marker, message.toString())
	}
	
	fun info(message: String) {
		this.LOGGER.info(message)
	}

//	fun <T : GameRules.Value<T>> registerGamerule(name: String, category: GameRules.Category, type: GameRules.Type<T>): GameRules.Key<T> {
//		GameRules.register("${this.ID}:$name", category, type)
//	}
	
}

