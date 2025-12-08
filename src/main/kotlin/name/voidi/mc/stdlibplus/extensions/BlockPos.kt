package name.voidi.mc.stdlibplus.extensions

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

/**
 * A progression of values from type `BlockPos`.
 * Model a line between two BlockPos where both share *exactly* one `Axis`
 */
class BlockPosProgression(override val start: BlockPos, override val endInclusive: BlockPos, val step: Int = 1) :
    ClosedRange<BlockPos>, Iterable<BlockPos> {

    val direction: Direction

    init {
        if (start == endInclusive)
            throw IllegalArgumentException("Start $start and End $endInclusive are the same BlockPos")

        direction =
		if (start.x != endInclusive.x && start.y == endInclusive.y && start.z == endInclusive.z) {
			if (start.x < endInclusive.x)
				Direction.EAST
			else if (start.x > endInclusive.x)
				Direction.WEST
			else
				throw IllegalArgumentException("Start $start and End $endInclusive do not have one component in common")
		} else if (start.x == endInclusive.x && start.y != endInclusive.y && start.z == endInclusive.z) {
			if (start.y < endInclusive.y)
				Direction.UP
			else if (start.y > endInclusive.y)
				Direction.DOWN
			else
				throw IllegalArgumentException("Start $start and End $endInclusive do not have one component in common")
		} else if (start.x == endInclusive.x && start.y == endInclusive.y && start.z != endInclusive.z) {
			if (start.z < endInclusive.z)
				Direction.SOUTH
			else if (start.z > endInclusive.z)
				Direction.NORTH
			else
				throw IllegalArgumentException("Start $start and End $endInclusive do not have one component in common")
		} else
			throw IllegalArgumentException("Start $start and End $endInclusive have the same BlockPos")
    }


    override fun contains(value: BlockPos): Boolean {
        return when (direction.axis) {
            Direction.Axis.X -> value.x in start.x..endInclusive.x
            Direction.Axis.Y -> value.y in start.y..endInclusive.y
            Direction.Axis.Z -> value.z in start.z..endInclusive.z
        }
    }

    infix fun step(step: Int): BlockPosProgression {
        return BlockPosProgression(this.start, this.endInclusive, step)
    }

    fun random(): BlockPos {
        return this.toList().random()
    }

    fun randomOrNull(): BlockPos? {
        if (isEmpty())
            return null
        return this.random()
    }

    fun reversed(): BlockPosProgression {
        return this.step(-this.step)
    }

    override fun iterator(): Iterator<BlockPos> {
        return BlockPosIterator(this.start, this.endInclusive, this.direction, this.step)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockPosProgression) return false

        if (start != other.start) return false
        if (endInclusive != other.endInclusive) return false

        return true
    }

    override fun hashCode(): Int {
        return 31 * start.hashCode() + endInclusive.hashCode()
    }

    override fun toString(): String {
        return "$start$direction$endInclusive"
    }
}

class BlockPosIterator(val start: BlockPos, val endInclusive: BlockPos, val direction: Direction, val step: Int) :
    Iterator<BlockPos> {
    private var cursor: BlockPos = start
    override fun hasNext(): Boolean {
        return when (direction) {
            Direction.NORTH -> cursor.z >= endInclusive.z
            Direction.SOUTH -> cursor.z <= endInclusive.z
            Direction.UP -> cursor.y <= endInclusive.y
            Direction.DOWN -> cursor.y >= endInclusive.y
            Direction.WEST -> cursor.x >= endInclusive.x
            Direction.EAST -> cursor.x <= endInclusive.x
        }
    }

    override fun next(): BlockPos {
        val nextElem = cursor
        cursor = cursor.relative(direction, step)
        return nextElem
    }
}

fun BlockPos.withX(xValue: Int): BlockPos {
	return BlockPos(xValue, this.y, this.z)
}

fun BlockPos.withY(yValue: Int): BlockPos {
	return BlockPos(this.x, yValue, this.z)
}

fun BlockPos.withZ(zValue: Int): BlockPos {
	return BlockPos(this.x, this.y, zValue)
}

fun BlockPos.withXY(xValue: Int, yValue: Int): BlockPos {
	return BlockPos(xValue, yValue, this.z)
}

fun BlockPos.withYZ(yValue: Int, zValue: Int): BlockPos {
	return BlockPos(this.x, yValue, zValue)
}

fun BlockPos.withXZ(xValue: Int, zValue: Int): BlockPos {
	return BlockPos(xValue, this.y, zValue)
}

public operator fun BlockPos.rangeTo(other: BlockPos): BlockPosProgression {
	return BlockPosProgression(this, other, 1)
}

infix fun BlockPos.until(to: BlockPos): BlockPosProgression {
	val re = BlockPosProgression(this, to, 1).reversed().toList()
	return BlockPosProgression(this, re[1], 1)
}

infix fun BlockPos.downTo(to: BlockPos): BlockPosProgression {
	return BlockPosProgression(this, to, -1)
}

//fun BlockPos.directionForOffset(other: BlockPos): Direction? {
//	return Direction.getNearest(other.x-this.x, other.y-this.y,other.z-this.z)
//}

//fun BlockPos.orthogonalSquare(lineAxis: Direction.Axis, edgeExtend: Int): Set<BlockPos> {
//	val extendingAxis = lineAxis.remaining
//	val consistingBlocks = mutableSetOf<BlockPos>()
//	for (offsetWidth in -edgeExtend..edgeExtend) {
//		for (offsetHeight in -edgeExtend..edgeExtend) {
//			consistingBlocks += this.offset(extendingAxis.first(), offsetWidth).offset(extendingAxis.last(), offsetHeight)
//		}
//	}
//	return consistingBlocks
//}