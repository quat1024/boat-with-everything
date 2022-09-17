package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.special.BoatRules;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public interface LoaderServices {
	void blocks(Consumer<Registerer<Block>> reg);
	void blockEntityTypes(Consumer<Registerer<BlockEntityType<?>>> reg);
	
	interface Registerer<T> {
		void register(ResourceLocation id, T thing);
	}
	
	<T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(Factory<T> factory, Block... blocks);
	public interface Factory<T extends BlockEntity> {
		T create(BlockPos blockPos, BlockState blockState);
	}
	
	default void addMoreRules(WeirdBlockRegistryThing<BoatRules> rules) {
		
	}
}
