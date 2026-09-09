package name.voidi.mc.voidilibplus.datagen

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.advancements.AdvancementSubProvider
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.function.Consumer
import kotlin.collections.iterator


open class GenericAdvancementsProvider(val modID: String, val factory: DataGenFactory): AdvancementSubProvider {
	lateinit var ItemRegistry: HolderLookup.RegistryLookup<Item>
	lateinit var BlockRegistry: HolderLookup.RegistryLookup<Block>

	protected lateinit var writer: Consumer<AdvancementHolder>

	override fun generate(registries: HolderLookup.Provider, writer: Consumer<AdvancementHolder>) {
		this.writer = writer
		this.ItemRegistry = registries.lookupOrThrow(Registries.ITEM)
		this.BlockRegistry = registries.lookupOrThrow(Registries.BLOCK)
		
		for (builder in factory.builders) {
			context(this.ItemRegistry, this.BlockRegistry) {
				builder.builderBlock(builder)
			}
			val parent = builder.Parent?.let {
				if(it.namespace == NAMESPACE_PLACEHOLDER)
					Identifier.fromNamespaceAndPath(this.modID, it.path)
				else
					it
			}
			val title = builder.Display.Title ?: Component.translatable("advancements.${this.modID}.${builder.Path}.title")
			val description = builder.Display.Description ?: Component.translatable("advancements.${this.modID}.${builder.Path}.description")

			val realBuilder = Advancement.Builder.advancement()

			parent?.let { realBuilder.parent(Advancement.Builder.advancement().build(it))	}

			realBuilder.display(builder.Display.Icon, title, description, builder.Display.Background, builder.Display.Type, builder.Display.ShowToast, builder.Display.AnnounceChat, builder.Display.Hidden)
			
			for ((key, value) in builder.criterionList) {
				realBuilder.addCriterion(key, value)
			}
			realBuilder.requirements(AdvancementRequirements(builder.Requirements))

			realBuilder.save(this.writer, Identifier.fromNamespaceAndPath(this.modID, builder.Path))
		}

	}
	
}
