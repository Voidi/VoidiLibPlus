package name.voidi.mc.stdlibplus

import name.voidi.mc.stdlibplus.extensions.*
import net.minecraft.client.*
import net.minecraft.client.gui.components.debug.*
import net.minecraft.resources.*
import net.neoforged.api.distmarker.*
import net.neoforged.bus.api.*
import net.neoforged.fml.*
import net.neoforged.fml.common.*
import net.neoforged.neoforge.client.event.*

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
			Identifier.fromNamespaceAndPath(MODID, "development_debug").let { path ->
				event.register(path, DevelopmentDebugEntry)
				event.includeInProfile(path, DebugScreenProfile.DEFAULT, DebugScreenEntryStatus.IN_OVERLAY)
			}
		}
		
		DevelopmentDebugEntry.AppendLines[DebugScreenEntries.PLAYER_POSITION] = { _, _, _, _ ->
			"Movement: ${Minecraft.getInstance().player?.deltaMovement?.display}"
		}
	}
}