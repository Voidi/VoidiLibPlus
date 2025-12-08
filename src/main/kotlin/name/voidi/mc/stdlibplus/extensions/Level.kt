package name.voidi.mc.stdlibplus.extensions

import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

fun Level.getFirstBlock(searchCube: BlockBox, condition: (BlockPos, BlockState) -> Boolean): BlockPos? {
	for (position in searchCube) {
		if (condition(position, this.getBlockState(position)))
			return position
	}
	return null
}

fun Level.getFirstBlock(searchCube: BlockBox, blockState: BlockState): BlockPos? {
	return this.getFirstBlock(searchCube) { blockPos, foundBlockState ->
		blockState == foundBlockState
	}
}

fun Level.getFirstBlock(searchCube: BlockBox, tagKey: TagKey<Block>): BlockPos? {
	return this.getFirstBlock(searchCube) { blockPos, foundBlockState ->
		foundBlockState.block in tagKey
	}
}

fun Level.getFirstBlock(searchCube: BlockBox, blocks: Iterable<ResourceLocation>): BlockPos? {
	return this.getFirstBlock(searchCube) { blockPos, foundBlockState ->
		foundBlockState.block.ResourceLocation in blocks
	}
}