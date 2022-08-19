package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.HackyEntityUpdateIds;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SpecialNoteBlockRules implements SpecialBoatRules {
	@Override
	public void tick(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !(state.getBlock() instanceof NoteBlock) ||
			!state.hasProperty(BlockStateProperties.POWERED) || 
			!state.hasProperty(BlockStateProperties.NOTEBLOCK_INSTRUMENT)) return;
		
		NoteBlockInstrument currentInst = state.getValue(BlockStateProperties.NOTEBLOCK_INSTRUMENT);
		
		BlockPos below = new BlockPos(SpecialBoatRules.positionOfBlock(boat)).below();
		NoteBlockInstrument worldInst = NoteBlockInstrument.byState(boat.level.getBlockState(below));
		if(currentInst != worldInst) {
			state = state.setValue(BlockStateProperties.NOTEBLOCK_INSTRUMENT, worldInst);
			ext.setBlockState(state);
		}
		
		boolean isPowered = state.getValue(BlockStateProperties.POWERED);
		boolean shouldPower = SpecialBoatRules.isPowered(boat);
		if(isPowered != shouldPower) {
			state = state.setValue(BlockStateProperties.POWERED, shouldPower);
			ext.setBlockState(state);
			
			if(shouldPower) dootServer(boat);
		}
	}
	
	@Override
	public @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {
		BlockState state = ext.getBlockState();
		if(state == null || !(state.getBlock() instanceof NoteBlock) || !state.hasProperty(BlockStateProperties.NOTE)) return InteractionResult.PASS;
		
		ext.setBlockState(state.cycle(BlockStateProperties.NOTE));
		dootServer(boat);
		return InteractionResult.SUCCESS;
	}
	
	private void dootServer(Boat boat) {
		boat.level.broadcastEntityEvent(boat, HackyEntityUpdateIds.NOTEBLOCK_DOOT);
	}
	
	public void dootClient(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !(state.getBlock() instanceof NoteBlock) ||
			!state.hasProperty(BlockStateProperties.NOTEBLOCK_INSTRUMENT) ||
			!state.hasProperty(BlockStateProperties.NOTE)) return;
		
		//mostly copied out of note block obviously
		//TODO: note particle comes from the offset position of the note block
		int note = state.getValue(BlockStateProperties.NOTE);
		float pitch = (float) Math.pow(2, (note - 12) / 12f);
		
		Vec3 pos = SpecialBoatRules.positionOfBlock(boat);
		boat.level.playLocalSound(pos.x, pos.y, pos.z, state.getValue(BlockStateProperties.NOTEBLOCK_INSTRUMENT).getSoundEvent(), SoundSource.RECORDS, 3f, pitch, false);
		boat.level.addParticle(ParticleTypes.NOTE, pos.x, pos.y + 1, pos.z, note / 24d, 0, 0);
	}
}
