package name.voidi.mc.voidilibplus.lifecycle.config

import net.neoforged.neoforge.common.ModConfigSpec

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