package name.voidi.mc.stdlibplus

import net.minecraft.core.registries.*
import net.minecraft.resources.*
import net.neoforged.neoforge.server.*


val Dimensions: List<Identifier>
	get() {
//		if (EffectiveSide.get() == LogicalSide.SERVER) {
			val regis = ServerLifecycleHooks.getCurrentServer()!!.registryAccess().lookup(Registries.DIMENSION).get()
			return buildList {
				for (entry in regis.entrySet()) {
					add(entry.key.identifier())
				}
			}
//		}
//		return emptyList()
	}
