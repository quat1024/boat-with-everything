package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;

import java.util.Optional;

public class SpecialDoorRules implements SpecialBoatRules {
	@Override
	public void tick(BlockState state, Boat boat) {
		boolean isOpen = state.getValue(BlockStateProperties.OPEN);
		boolean shouldPower = SpecialBoatRules.isPowered(boat);
		if(isOpen != shouldPower) {
			boat.getEntityData().set(BoatWithEverything.DATA_ID_BLOCK_STATE, Optional.of(
				state.setValue(BlockStateProperties.OPEN, shouldPower)
					.setValue(BlockStateProperties.POWERED, shouldPower)));
			playSound(state, boat, shouldPower);
		}
	}
	
	@Override
	public InteractionResult interact(BlockState state, Boat boat) {
		boolean isOpen = state.getValue(BlockStateProperties.OPEN);
		boat.getEntityData().set(BoatWithEverything.DATA_ID_BLOCK_STATE, Optional.of(state.setValue(BlockStateProperties.OPEN, !isOpen)));
		playSound(state, boat, !isOpen);
		return InteractionResult.SUCCESS;
	}
	
	private void playSound(BlockState state, Boat boat, boolean opening) {
		if(opening) boat.playSound(state.getMaterial() == Material.METAL ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.WOODEN_DOOR_OPEN);
		else boat.playSound(state.getMaterial() == Material.METAL ? SoundEvents.IRON_DOOR_CLOSE : SoundEvents.WOODEN_DOOR_CLOSE);
	}
}
