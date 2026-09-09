package name.voidi.mc.voidilibplus.datagen

import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.predicates.MobEffectsPredicate
import net.minecraft.advancements.triggers.Criterion
import net.minecraft.advancements.triggers.EffectsChangedTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block

internal const val NAMESPACE_PLACEHOLDER = "DATAGEN_MODID_Placeholder"

class AdvancementBuilder(
	val builderBlock: context(HolderLookup.RegistryLookup<Item>, HolderLookup.RegistryLookup<Block>) AdvancementBuilder.() -> Unit
) {
	lateinit var Path: String
	var Parent: Identifier? = null
	lateinit var Display: DisplayInfoBuilder
	var Requirements: MutableList<MutableList<String>> = mutableListOf()

	internal val criterionList = mutableMapOf<String, Criterion<*>>()
	
	fun from(namespace: String, path: String): Identifier {
		return Identifier.fromNamespaceAndPath(namespace, path)
	}
	
	fun from(path: String): Identifier {
		return Identifier.fromNamespaceAndPath("minecraft", path)
	}

	fun from(other: AdvancementBuilder): Identifier {
		return Identifier.fromNamespaceAndPath(NAMESPACE_PLACEHOLDER, other.Path)
	}

	fun display(block: DisplayInfoBuilder.() -> Unit): DisplayInfoBuilder {
		val builder = DisplayInfoBuilder()
		builder.block()
		return builder
	}

	fun requirement(requirementKey: String? = null, builderAction: RequirementBuilder.() -> Unit): MutableList<String> {
		val key = requirementKey ?: this.Path
		val requirementBuilder = RequirementBuilder(key, criterionList)
		requirementBuilder.builderAction()
		return requirementBuilder.requirements
	}

	
//	fun requirements(vararg requirements: Criterion<*>) {
//		for (criterion in requirements) {
//			this.Requirements += mutableListOf(criterion)
//		}
//	}
//
//	fun requirementsAny(vararg requirements: List<Criterion<*>>) {
//		for (criterions in requirements) {
//			this.Requirements += mutableListOf<Criterion<*>>().let {
//				it.addAll(criterions)
//				it
//			}
//		}
//	}

	class DisplayInfoBuilder() {
		lateinit var Icon: ItemLike
		var Title: Component? = null
		var Description: Component? = null
		var Background: Identifier? = null
		var Type: AdvancementType = AdvancementType.TASK
		var ShowToast: Boolean = true
		var AnnounceChat: Boolean = false
		var Hidden: Boolean = false
	}
	
	class RequirementBuilder(
		val baseKey: String,
		val map: MutableMap<String, Criterion<*>>
	) : MutableMap<String, Criterion<*>> by map {
		
		val requirements: MutableList<String> = mutableListOf()
		override fun put(key: String, value: Criterion<*>): Criterion<*>? {
			this.requirements += key
			return map.put(key, value)
		}
		
		operator fun Criterion<*>.unaryPlus(){
			this@RequirementBuilder["${this@RequirementBuilder.baseKey}${this@RequirementBuilder.map.size}"] = this
		}
		
		fun effects_changed(builder: MobEffectsPredicate.Builder) {
			+EffectsChangedTrigger.TriggerInstance.hasEffects(builder)
		}
	}
}
