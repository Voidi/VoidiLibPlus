package name.voidi.mc.voidilibplus

import net.minecraft.core.registries.*
import net.minecraft.resources.*
import net.neoforged.neoforge.server.*


val Dimensions: Set<Identifier>
	get() {
		// this seems to work on the physicial client
		val regis = ServerLifecycleHooks.getCurrentServer()?.registryAccess()?.lookupOrThrow(Registries.LEVEL_STEM)
		return regis?.keySet().orEmpty()
	}
