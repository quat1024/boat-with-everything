package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.special.BoatRules;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;

public interface LoaderServices {
	void blocks(Consumer<Registerer<Block>> reg);
	void blockEntityTypes(Consumer<Registerer<BlockEntityType<?>>> reg);
	
	interface Registerer<T> {
		void register(ResourceLocation id, T thing);
	}
	
	default void addMoreRules(WeirdBlockRegistryThing<BoatRules> rules) {
		
	}
}
