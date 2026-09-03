package name.voidi.mc.voidilibplus.lifecycle.config

import name.voidi.mc.voidilibplus.MOD
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.event.config.ModConfigEvent

/**
 * Static thing to do conversion between usable Config objects and there string representation in the config file
 */
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
	
	//not needed, fired when unconnect or server stopped,
//	@SubscribeEvent
//	fun onUnloadCache(event: ModConfigEvent.Unloading) {
//	}
}