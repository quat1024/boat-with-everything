package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;

import java.util.Optional;

@SuppressWarnings("ClassCanBeRecord")
public class SpecialDoorRules implements SpecialBoatRules {
	public SpecialDoorRules(SoundEvent openWood, SoundEvent openMetal, SoundEvent closeWood, SoundEvent closeMetal) {
		this.openWood = openWood;
		this.openMetal = openMetal;
		this.closeWood = closeWood;
		this.closeMetal = closeMetal;
	}
	
	private final SoundEvent openWood, openMetal, closeWood, closeMetal;
	
	@Override
	public void tick(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !state.hasProperty(BlockStateProperties.OPEN) || !state.hasProperty(BlockStateProperties.POWERED)) return;
		
		boolean isOpen = state.getValue(BlockStateProperties.OPEN);
		boolean shouldPower = SpecialBoatRules.isPowered(boat);
		if(isOpen != shouldPower) {
			ext.setBlockState(state.setValue(BlockStateProperties.OPEN, shouldPower).setValue(BlockStateProperties.POWERED, shouldPower));
			playSound(state, boat, shouldPower);
		}
	}
	
	@Override
	public InteractionResult interact(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !state.hasProperty(BlockStateProperties.OPEN)) return InteractionResult.PASS;
		if(state.getMaterial() == Material.METAL) return InteractionResult.PASS;
		
		if(boat.level.isClientSide) return InteractionResult.SUCCESS;
		
		boolean isOpen = state.getValue(BlockStateProperties.OPEN);
		ext.setBlockState(state.setValue(BlockStateProperties.OPEN, !isOpen));
		playSound(state, boat, !isOpen);
		return InteractionResult.SUCCESS;
	}
	
	private void playSound(BlockState state, Boat boat, boolean opening) {
		if(opening) {
			boat.playSound(state.getMaterial() == Material.METAL ? openMetal : openWood);
			boat.level.gameEvent(boat, GameEvent.BLOCK_OPEN, boat.position());
		}	else {
			boat.playSound(state.getMaterial() == Material.METAL ? closeMetal : closeWood);
			boat.level.gameEvent(boat, GameEvent.BLOCK_CLOSE, boat.position());
		}
	}
}
