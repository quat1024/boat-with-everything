package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;

@SuppressWarnings("ClassCanBeRecord")
public class SpecialDoorRules implements BoatRules {
	public SpecialDoorRules(SoundEvent openWood, SoundEvent openMetal, SoundEvent closeWood, SoundEvent closeMetal) {
		this.openWood = openWood;
		this.openMetal = openMetal;
		this.closeWood = closeWood;
		this.closeMetal = closeMetal;
	}
	
	public static final SpecialDoorRules DOORS = new SpecialDoorRules(SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.IRON_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.IRON_DOOR_CLOSE);
	public static final SpecialDoorRules TRAPDOORS = new SpecialDoorRules(SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_CLOSE);
	public static final SpecialDoorRules FENCE_GATES = new SpecialDoorRules(SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_CLOSE);
	
	private final SoundEvent openWood, openMetal, closeWood, closeMetal;
	
	@Override
	public void tick(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !state.hasProperty(BlockStateProperties.OPEN) || !state.hasProperty(BlockStateProperties.POWERED)) return;
		
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		boolean shouldPower = BoatRules.isPowered(boat);
		if(isPowered != shouldPower) {
			boolean isOpen = state.getValue(BlockStateProperties.OPEN);
			ext.setBlockState(state.setValue(BlockStateProperties.OPEN, shouldPower).setValue(BlockStateProperties.POWERED, shouldPower));
			if(isOpen != shouldPower)	playSound(state, boat, shouldPower);
		}
	}
	
	@Override
	public InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {
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
			boat.gameEvent(GameEvent.BLOCK_OPEN);
		}	else {
			boat.playSound(state.getMaterial() == Material.METAL ? closeMetal : closeWood);
			boat.gameEvent(GameEvent.BLOCK_CLOSE);
		}
	}
}
