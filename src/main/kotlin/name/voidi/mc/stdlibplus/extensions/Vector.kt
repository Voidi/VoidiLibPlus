package name.voidi.mc.stdlibplus.extensions

import net.minecraft.world.phys.Vec3

val Vec3.display: String
	get() {
		return "(${String.format("%+3.2f", this.x)},${String.format("%+3.2f", this.y)},${String.format("%+3.2f", this.z)})"
	}