package name.voidi.mc.stdlibplus

import com.mojang.serialization.*
import io.netty.buffer.*
import net.minecraft.core.component.*
import net.minecraft.network.codec.*
import net.neoforged.neoforge.registries.*

fun <T> DeferredRegister.DataComponents.register(
	name: String,
	codec: Codec<T>,
	networkCodec: StreamCodec<ByteBuf, T>? = null
): DeferredHolder<DataComponentType<*>, DataComponentType<T>> {
	return this.registerComponentType(name) { builder ->
		if (networkCodec == null) {
			builder
				.persistent(codec)
		} else {
			builder
				.persistent(codec)
				// The codec to read/write the data across the network
				.networkSynchronized(networkCodec)
		}
	}
}



