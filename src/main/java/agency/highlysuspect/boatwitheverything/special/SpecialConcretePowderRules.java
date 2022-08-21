package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.mixin.AccessorConcretePowderBlock;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SpecialConcretePowderRules implements SpecialBoatRules {
	@Override
	public void tick(Boat boat, BoatExt ext) {
		if(!boat.isUnderWater()) return;
		
		BlockState state = ext.getBlockState();
		if(!(state.getBlock() instanceof AccessorConcretePowderBlock acpb)) return;
		
		BlockState conkCreteBaby = acpb.bwe$thatsConkCreteBabey();
		ext.setBlockState(conkCreteBaby);
		ext.setItemStack(new ItemStack(conkCreteBaby.getBlock()));
	}
}
