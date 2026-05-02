package name.voidi.mc.stdlibplus

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer
import net.minecraft.client.gui.components.debug.DebugScreenEntry
import net.minecraft.resources.Identifier
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk

object DevelopmentDebugEntry : DebugScreenEntry {
	val DebugLines: MutableList<(
		displayer: DebugScreenDisplayer,
		level: Level?,
		clientChunk: LevelChunk?,
		serverChunk: LevelChunk?
	) -> String> = mutableListOf()
	
	val AppendLines: MutableMap<Identifier, (
		displayer: DebugScreenDisplayer,
		level: Level?,
		clientChunk: LevelChunk?,
		serverChunk: LevelChunk?
	) -> String> = mutableMapOf()

	override fun display(
		displayer: DebugScreenDisplayer,
		level: Level?,
		clientChunk: LevelChunk?,
		serverChunk: LevelChunk?
	) {
		for ((id, supplier) in this.AppendLines) {
			displayer.addToGroup(id, supplier(displayer, level, clientChunk, serverChunk))
		}
		for (supplier in this.DebugLines) {
			displayer.addLine(supplier(displayer, level, clientChunk, serverChunk))
		}
	}
}