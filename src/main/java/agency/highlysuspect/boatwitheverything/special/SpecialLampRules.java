package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

public class SpecialLampRules implements SpecialBoatRules {
	@Override
	public void tick(BlockState state, Boat boat) {
		boolean isLit = state.getValue(BlockStateProperties.LIT);
		boolean shouldLit = SpecialBoatRules.isPowered(boat);
		if(isLit != shouldLit) {
			boat.getEntityData().set(BoatWithEverything.DATA_ID_BLOCK_STATE, Optional.of(state.setValue(BlockStateProperties.LIT, shouldLit)));
		}
	}
}
