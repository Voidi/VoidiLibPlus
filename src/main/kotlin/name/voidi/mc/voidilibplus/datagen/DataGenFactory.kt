package name.voidi.mc.voidilibplus.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

abstract class DataGenFactory {
	val builders = mutableListOf<AdvancementBuilder>()
	
	fun advancement(builderBlock: context(HolderLookup.RegistryLookup<Item>, HolderLookup.RegistryLookup<Block>) AdvancementBuilder.() -> Unit): AdvancementBuilder {
		val builder = AdvancementBuilder(builderBlock)
		builders += builder
		return builder
	}
}