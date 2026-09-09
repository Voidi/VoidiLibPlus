package name.voidi.mc.voidilibplus.datagen

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.predicates.BlockPredicate
import net.minecraft.advancements.predicates.ItemPredicate
import net.minecraft.advancements.predicates.MinMaxBounds
import net.minecraft.advancements.triggers.Criterion
import net.minecraft.core.HolderLookup
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block


fun Advancement.Builder.addCriterionRequirementAny(key: String, vararg criterions: Criterion<*>): Advancement.Builder {
	val indexed = criterions.withIndex()
	for (criterion in indexed) {
		this.addCriterion("$key${criterion.index}", criterion.value)
	}
	this.requirements(AdvancementRequirements.anyOf(indexed.map { "${key}${it.index}" }))
	return this
}
fun Advancement.Builder.addCriterionRequirementAll(key: String, vararg criterions: Criterion<*>): Advancement.Builder {
	val indexed = criterions.withIndex()
	for (criterion in indexed) {
		this.addCriterion("$key${criterion.index}", criterion.value)
	}
	this.requirements(AdvancementRequirements.allOf(indexed.map { "${key}${it.index}" }))
	return this
}

context(registry: HolderLookup.RegistryLookup<Item>)
fun itemPredicate(vararg items: ItemLike): ItemPredicate.Builder {
	return ItemPredicate.Builder.item().of(registry, *items)
}
context(registry: HolderLookup.RegistryLookup<Item>)
fun itemPredicate(items: TagKey<Item>): ItemPredicate.Builder {
	return ItemPredicate.Builder.item().of(registry, items)
}
context(registry: HolderLookup.RegistryLookup<Block>)
fun blockPredicate(vararg blocks: Block): BlockPredicate.Builder {
	return BlockPredicate.Builder.block().of(registry, *blocks)
}
context(registry: HolderLookup.RegistryLookup<Block>)
fun blockPredicate(blocks: TagKey<Block>): BlockPredicate.Builder {
	return BlockPredicate.Builder.block().of(registry, blocks)
}

fun ItemPredicate.Builder.withCount(min: Int?, max: Int): ItemPredicate.Builder {
	return this.withCount(MinMaxBounds.Ints.between(min ?: 0, max))
}
fun ItemPredicate.Builder.withCount(amount: Int): ItemPredicate.Builder {
	return this.withCount(MinMaxBounds.Ints.exactly(amount))
}