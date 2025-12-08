package name.voidi.mc.stdlibplus.extensions

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.Criterion
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.*
import java.util.*

val Block.ResourceLocation: ResourceLocation
	get() = BuiltInRegistries.BLOCK.getKey(this)

operator fun TagKey<Item>.contains(item: Item): Boolean {
	return item.defaultInstance in this
}

operator fun TagKey<Item>.contains(stack: ItemStack): Boolean {
	return stack.`is`(this)
}

operator fun Iterable<ResourceLocation>.contains(block: Block): Boolean {
	return block.ResourceLocation in this
}
//operator fun Iterable<ResourceLocation>.contains(block: BlockState): Boolean {
//	return block.block.ResourceLocation in this
//}

operator fun TagKey<Block>.contains(block: Block): Boolean {
	return block.defaultBlockState().`is`(this)
}

operator fun TagKey<Block>.contains(blockstate: BlockState): Boolean {
	return blockstate.`is`(this)
}

operator fun Block.contains(blockstate: BlockState): Boolean {
	return blockstate.`is`(this)
}

val Direction.Axis.remaining: EnumSet<Direction.Axis>
	get() {
		return EnumSet.complementOf(EnumSet.of(this))
	}

