package name.voidi.mc.stdlibplus

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.slf4j.Logger

interface ModEntryPoint {
	val MOD_ID: String
	val modEventBus: IEventBus
	val modContainer: ModContainer
	
	val LOGGER: Logger
	fun onCommonSetup(event: FMLCommonSetupEvent) {}
	fun onDateGenClient(event: GatherDataEvent.Client) {
	}
}