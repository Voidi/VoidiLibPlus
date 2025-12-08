package name.voidi.mc.stdlibplus

import net.minecraft.core.registries.*
import net.minecraft.resources.*
import net.neoforged.neoforge.server.*


val Dimensions: List<ResourceLocation>
	get() {
//		if (EffectiveSide.get() == LogicalSide.SERVER) {
			val regis = ServerLifecycleHooks.getCurrentServer()!!.registryAccess().lookup(Registries.DIMENSION).get()
			return buildList {
				for (entry in regis.entrySet()) {
					add(entry.key.location())
				}
			}
//		}
//		return emptyList()
	}
