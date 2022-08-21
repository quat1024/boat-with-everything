package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.mixin.AccessorSpongeBlock;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SpecialSpongeRules implements BoatRules {
	@Override
	public void tick(Boat boat, BoatExt ext) {
		if(((AccessorSpongeBlock) Blocks.SPONGE).boatWithEverything$removeWaterBreadthFirstSearch(boat.level, boat.blockPosition())) {
			ext.setBlockState(Blocks.WET_SPONGE.defaultBlockState());
			ext.setItemStack(new ItemStack(Blocks.WET_SPONGE));
			boat.level.levelEvent(2001, boat.blockPosition(), Block.getId(Blocks.WATER.defaultBlockState()));
		}
	}
}
