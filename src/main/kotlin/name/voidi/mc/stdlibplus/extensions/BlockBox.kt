package name.voidi.mc.stdlibplus.extensions

import net.minecraft.core.BlockBox
import net.minecraft.core.BlockPos

fun BlockBox.expand(amount: Int): BlockBox {
	return BlockBox(this.min.offset(-amount, -amount, -amount), this.max.offset(amount, amount, amount))
}

fun BlockBox.shrink(amount: Int): BlockBox {
	return BlockBox(this.min.offset(amount, amount, amount), this.max.offset(-amount, -amount, -amount))
}

fun BlockBox.include(positions: Iterable<BlockPos>): BlockBox {
	if (this.none())
		return this
	var newBox = BlockBox.of(positions.first())
	for (pos in positions) {
		 newBox = newBox.include(pos)
	}
	return newBox
}

//get every blockpos inside
fun BlockBox.boundaryFill(
	startingPosition: BlockPos,
	boundary: List<BlockPos>,
	includeDiagonal: Boolean = false
): List<BlockPos> {
	
	val filledBlocks = mutableListOf<BlockPos>()
	
	fun fillPosition4(position: BlockPos) {
		if (position in this && position !in filledBlocks && position !in boundary) {
			filledBlocks += position
			fillPosition4(position.above())
			fillPosition4(position.below())
			fillPosition4(position.north())
			fillPosition4(position.east())
			fillPosition4(position.south())
			fillPosition4(position.west())
		}
	}
	
	fun fillPosition8(position: BlockPos) {
		if (position in this && position !in filledBlocks && position !in boundary) {
			filledBlocks += position
			BlockBox.of(position).expand(1).toList().forEach { fillPosition8(it) }
		}
	}
	//if (includeDiagonal)
	//	fillPosition8(startingPosition)
	//else
	fillPosition4(startingPosition)
	return filledBlocks
}

val BlockBox.Vertices: Set<BlockPos>
	get() {
		return buildSet {
			add(this@Vertices.min)
			add(this@Vertices.min.withX(this@Vertices.max.x))
			add(this@Vertices.max.withY(this@Vertices.min.y))
			add(this@Vertices.min.withZ(this@Vertices.max.z))

			add(this@Vertices.max)
			add(this@Vertices.max.withZ(this@Vertices.min.z))
			add(this@Vertices.min.withY(this@Vertices.max.y))
			add(this@Vertices.max.withX(this@Vertices.min.x))
		}
	}

val BlockBox.Edges: List<BlockPosProgression>
	get() {
		val vertexes = this.Vertices.sorted()
		val edgeList = listOf(
			vertexes[0]..vertexes[1],
			vertexes[0]..vertexes[2],
			vertexes[2]..vertexes[3],
			vertexes[1]..vertexes[3]
		)
		return edgeList
	}


val Iterable<BlockPos>.encompassingBox: BlockBox?
	get() {
		if (this.none())
			return null
		var box = BlockBox.of(this.first())
		for (pos in this) {
			box = box.include(pos)
		}
		return box
	}