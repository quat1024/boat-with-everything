package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.mixin.AccessorSpongeBlock;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SpecialSpongeRules implements SpecialBoatRules {
	@Override
	public void tick(BlockState state, Boat boat) {
		if(((AccessorSpongeBlock) Blocks.SPONGE).boatWithEverything$removeWaterBreadthFirstSearch(boat.level, boat.blockPosition())) {
			boat.getEntityData().set(BoatWithEverything.DATA_ID_BLOCK_STATE, Optional.of(Blocks.WET_SPONGE.defaultBlockState()));
			boat.getEntityData().set(BoatWithEverything.DATA_ID_ITEM_STACK, new ItemStack(Blocks.WET_SPONGE));
			boat.level.levelEvent(2001, boat.blockPosition(), Block.getId(Blocks.WATER.defaultBlockState()));
		}
	}
}
