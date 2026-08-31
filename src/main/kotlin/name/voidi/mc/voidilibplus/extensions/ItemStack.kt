package name.voidi.mc.voidilibplus.extensions

import name.voidi.mc.voidilibplus.DataComponentFactory
import net.minecraft.world.item.ItemStack

fun <T : DataComponentFactory.DataComponent> ItemStack.set(dataComponent: Pair<DataComponentFactory<*, T>, T>): ItemStack {
	this.set(dataComponent.first.TYPE, dataComponent.second)
	return this
}
