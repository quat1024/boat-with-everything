package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SpecialTntRules implements SpecialBoatRules {
	@Override
	public void tick(BlockState state, Boat boat) {
		if(SpecialBoatRules.isPowered(boat)) {
			boat.getEntityData().set(BoatWithEverything.DATA_ID_BLOCK_STATE, Optional.empty());
			boat.getEntityData().set(BoatWithEverything.DATA_ID_ITEM_STACK, ItemStack.EMPTY);
			TntBlock.explode(boat.level, boat.blockPosition());
		}
	}
}
