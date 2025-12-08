package name.voidi.mc.stdlibplus.extensions

import com.mojang.authlib.GameProfile
import net.minecraft.Util
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.fml.util.thread.EffectiveSide
import net.neoforged.neoforge.common.UsernameCache
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

/**
 * Modify *one* item in the stack and return the modified one.
 * if the stack has more than one item, the modified item is split in a new stack and try to add to the player's inventory.
 */
fun Player.modifyOneItem(original: ItemStack, block: ItemStack.() -> Unit): ItemStack {
	return if (!this.hasInfiniteMaterials() && original.count == 1) {
		original.block()
		original
	} else {
		val newItem = original.transmuteCopy(original.item, 1)
		original.consume(1, this)
		newItem.block()
		if (!this.inventory.add(newItem)) {
			this.drop(newItem, false)
		}
		newItem
	}
}

fun getUserName(uuid: UUID): String? {
		return UsernameCache.getLastKnownUsername(uuid) ?: getUserProfil(uuid)?.name
}

internal val uuidFails = mutableListOf<UUID>()

fun getUserProfil(uuid: UUID): GameProfile? {
	var profile: GameProfile? = null
	if(!uuidFails.contains(uuid) && EffectiveSide.get().isServer) { // see if MC/Yggdrasil knows about it?!
		val profilResolver = ServerLifecycleHooks.getCurrentServer()!!.services().profileResolver
		Util.nonCriticalIoPool().execute {
			profile = profilResolver.fetchById(uuid).getOrNull()
		}
	}
	if (profile == null && uuid !in uuidFails) {
		uuidFails.add(uuid)
	}
	return profile
}