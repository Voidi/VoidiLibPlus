package name.voidi.mc.voidilibplus.extensions

import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.tags.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.*
import java.util.*

val Block.ResourceLocation: Identifier
	get() = BuiltInRegistries.BLOCK.getKey(this)

operator fun TagKey<Item>.contains(item: Item): Boolean {
	return item.defaultInstance in this
}

operator fun TagKey<Item>.contains(stack: ItemStack): Boolean {
	return stack.`is`(this)
}

@JvmName("IterableIdentifier_ContainsBlock")
operator fun Iterable<Identifier>.contains(block: Block): Boolean {
	return block.ResourceLocation in this
}

operator fun TagKey<Block>.contains(block: Block): Boolean {
	return block.defaultBlockState().`is`(this)
}

operator fun TagKey<Block>.contains(blockstate: BlockState): Boolean {
	return blockstate.`is`(this)
}

@JvmName("IterableTagKeyBlock_ContainsBlock")
operator fun Iterable<TagKey<Block>>.contains(block: Block): Boolean {
	return this.any { block.defaultBlockState().`is`(it) }
}

@JvmName("IterableTagKeyBlock_ContainsBlockstatr")
operator fun Iterable<TagKey<Block>>.contains(blockstate: BlockState): Boolean {
	return this.any { blockstate.`is`(it) }
}

operator fun Block.contains(blockstate: BlockState): Boolean {
	return blockstate.`is`(this)
}

val Direction.Axis.remaining: EnumSet<Direction.Axis>
	get() {
		return EnumSet.complementOf(EnumSet.of(this))
	}

