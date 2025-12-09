package name.voidi.mc.stdlibplus

import name.voidi.mc.stdlibplus.extensions.display
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus
import net.minecraft.client.gui.components.debug.DebugScreenProfile
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent

// The value here should match an entry in the META-INF/neoforge.mods.toml file
const val MODID = "voidilibplus"
lateinit var MOD: EntryPoint

@Mod(MODID)
class EntryPoint(
	modEventBus: IEventBus, modContainer: ModContainer, dist: Dist
) : AbstractEntryPoint(MODID, modEventBus, modContainer, dist) {
	
	init {
		MOD = this
	}
}

@Mod(MODID, dist = [Dist.CLIENT])
class ClientEntryPoint(
	modEventBus: IEventBus, modContainer: ModContainer, dist: Dist
) : AbstractEntryPoint(MODID, modEventBus, modContainer, dist) {
	
	init {
		// register a generic debug screen entry
		modEventBus.addListener { event: RegisterDebugEntriesEvent ->
			ResourceLocation.fromNamespaceAndPath(MODID, "development_debug").let { path ->
				event.register(path, DevelopmentDebugEntry)
				event.includeInProfile(path, DebugScreenProfile.DEFAULT, DebugScreenEntryStatus.NEVER)
			}
		}
	
		DevelopmentDebugEntry.DebugLines += { _, _, _, _ ->
			"Movement: ${Minecraft.getInstance().player!!.deltaMovement.display}"
		}
	}
}