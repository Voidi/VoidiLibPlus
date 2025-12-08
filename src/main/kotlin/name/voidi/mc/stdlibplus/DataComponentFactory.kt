package name.voidi.mc.stdlibplus

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister


abstract class DataComponentFactory<F : DataComponentFactory<F,T>, T : DataComponentFactory.DataComponent>(val id: String) {
	abstract val CODEC: Codec<T>
	
	protected var typeholder: DeferredHolder<DataComponentType<*>, DataComponentType<T>>? = null
	val TYPE: DataComponentType<T>
		get() {
			if (this.typeholder == null)
				throw IllegalStateException("DataComponent $id not registered yet!")
			return this.typeholder!!.get()
		}

	@Suppress("UNCHECKED_CAST")
	fun register(registry: DeferredRegister.DataComponents): F {
		this.typeholder = registry.register(this.id, CODEC)
		return this as F
	}
	
	interface DataComponent {
	}
}
