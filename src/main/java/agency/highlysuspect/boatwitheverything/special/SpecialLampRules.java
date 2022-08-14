package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SpecialLampRules implements SpecialBoatRules {
	@Override
	public void tick(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(!state.hasProperty(BlockStateProperties.LIT)) return;
		
		boolean isLit = state.getValue(BlockStateProperties.LIT);
		boolean shouldLit = SpecialBoatRules.isPowered(boat);
		if(isLit != shouldLit) {
			ext.setBlockState(state.setValue(BlockStateProperties.LIT, shouldLit));
		}
	}
}
