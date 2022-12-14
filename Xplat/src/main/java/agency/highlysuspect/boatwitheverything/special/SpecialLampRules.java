package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SpecialLampRules implements BoatRules {
	@Override
	public void tick(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !state.hasProperty(BlockStateProperties.LIT)) return;
		
		boolean isLit = state.getValue(BlockStateProperties.LIT);
		boolean shouldLit = BoatRules.isPowered(boat);
		if(isLit != shouldLit) {
			ext.setBlockState(state.setValue(BlockStateProperties.LIT, shouldLit));
		}
	}
}
