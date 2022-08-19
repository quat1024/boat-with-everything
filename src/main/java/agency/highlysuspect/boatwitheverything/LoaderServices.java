package agency.highlysuspect.boatwitheverything;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface LoaderServices {
	void blocks(Consumer<Registerer<Block>> reg);
	void blockEntityTypes(Consumer<Registerer<BlockEntityType<?>>> reg);
	
	interface Registerer<T> {
		void register(ResourceLocation id, T thing);
	}
}
