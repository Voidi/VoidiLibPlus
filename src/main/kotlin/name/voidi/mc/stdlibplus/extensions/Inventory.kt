package name.voidi.mc.stdlibplus.extensions

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

/**
 * Adds the stack to the first empty slot in the player's inventory.
 */
operator fun Inventory.plusAssign(itemStack: ItemStack) {
	this.add(itemStack)
}


fun Inventory.getFirstStack(item: Item): Int? {
	for (j in 0 until this.containerSize) {
		if (!this.getItem(j).`is`(item))
			continue
		return j
	}
	return null
}

fun Inventory.getFirstStack(toSearch: ItemStack): Int? {
	for (j in 0 until this.containerSize) {
		if (!ItemStack.isSameItem(this.getItem(j), toSearch))
			continue
		return j
	}
	return null
}