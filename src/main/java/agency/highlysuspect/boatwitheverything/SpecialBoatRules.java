package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.special.SpecialDoorRules;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface SpecialBoatRules {
	default boolean consumesPassengerSlot() {
		return true;
	}
	
	default void tick(BlockState state, Boat boat) {
		//no-op
	}
	
	default InteractionResult interact(BlockState state, Boat boat) {
		return InteractionResult.PASS;
	}
	
	SpecialBoatRules DEFAULT = new SpecialBoatRules() {};
	SpecialBoatRules DEFAULT_NO_CONSUME = new SpecialBoatRules() {
		@Override
		public boolean consumesPassengerSlot() {
			return false;
		}
	};
	SpecialBoatRules DOOR = new SpecialDoorRules();
	
	static SpecialBoatRules get(BlockState state) {
		if(state.is(BlockTags.BANNERS) || state.is(BlockTags.WOOL_CARPETS)) {
			return DEFAULT_NO_CONSUME;
		}
		
		if((state.is(BlockTags.TRAPDOORS) || state.is(BlockTags.DOORS)) && (state.hasProperty(BlockStateProperties.POWERED) && state.hasProperty(BlockStateProperties.OPEN))) {
			return DOOR;
		}
		
		return DEFAULT;
	}
	
	static boolean isPowered(Boat boat) {
		return BlockPos.betweenClosedStream(boat.getBoundingBox()).anyMatch(pos -> boat.level.hasNeighborSignal(pos));
	}
}
