package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.Starboarding;
import agency.highlysuspect.boatwitheverything.mixin.AccessorCampfireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SpecialCampfireRules implements BoatRules {
	@Override
	public @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {
		BlockState state = ext.getBlockState();
		if(state == null || !(state.getBlock() instanceof CampfireBlock)) return InteractionResult.PASS;
		
		ItemStack held = player.getItemInHand(hand);
		if(state.getValue(BlockStateProperties.LIT)) {
			//Extinguish with a water bucket
			if(held.getItem() == Items.WATER_BUCKET) {
				dowse(boat, ext, state);
				player.setItemInHand(hand, BucketItem.getEmptySuccessItem(held, player));
				return InteractionResult.SUCCESS;
			}
		} else {
			//Lighting with a flintsteel or fire charge
			if(held.getItem() == Items.FLINT_AND_STEEL) {
				boat.playSound(SoundEvents.FLINTANDSTEEL_USE, 1f, boat.level.random.nextFloat() * 0.4f + 0.8f);
				ext.setBlockState(state.setValue(BlockStateProperties.LIT, true));
				held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				return InteractionResult.SUCCESS;
			} else if(held.getItem() == Items.FIRE_CHARGE) {
				boat.playSound(SoundEvents.FIRECHARGE_USE, 1f, (boat.level.random.nextFloat() - boat.level.random.nextFloat()) * 0.2f + 1f); //whar ?
				ext.setBlockState(state.setValue(BlockStateProperties.LIT, true));
				held.shrink(1);
				player.setItemInHand(hand, held); //sync?
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.PASS;
	}
	
	public void dowse(Boat boat, BoatExt ext, BlockState state) {
		ext.setBlockState(state.setValue(BlockStateProperties.LIT, false));
		
		Starboarding.playSound(boat, SoundEvents.GENERIC_EXTINGUISH_FIRE);
		for(int i = 0; i < 20; i++) particle(boat, ext, state, true);
	}
	
	//Based on copy from CampfireBlock.makeParticles, uses vec3 instead of blockpos.
	public void particle(Boat boat, BoatExt ext, BlockState state, boolean smoky) {
		Random r = boat.level.getRandom();
		Vec3 campfirePos = BoatRules.positionOfBlock(boat);
		SimpleParticleType type = state.getValue(BlockStateProperties.SIGNAL_FIRE) ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
		
		boat.level.addAlwaysVisibleParticle(
			type, true,
			campfirePos.x + r.nextDouble() / rflip(r, 3),
			campfirePos.y + r.nextDouble() + r.nextDouble(),
			campfirePos.z + r.nextDouble() / rflip(r, 3),
			0, 0.07, 0
		);
		
		if(smoky) {
			boat.level.addParticle(ParticleTypes.SMOKE,
				r.nextDouble() / rflip(r, 4),
				campfirePos.y + 0.4,
				r.nextDouble() / rflip(r, 4),
				0, 0.005, 0
			);
		}
	}
	
	private static double rflip(Random r, double v) {
		return r.nextBoolean() ? -v : v;
	}
	
	@Override
	public void tick(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !(state.getBlock() instanceof CampfireBlock cb)) return;
		
		BlockPos below = new BlockPos(BoatRules.positionOfBlock(boat)).below();
		boolean isSignal = state.getValue(BlockStateProperties.SIGNAL_FIRE);
		boolean shouldSignal = ((AccessorCampfireBlock) cb).bwe$isSmokeSource(boat.level.getBlockState(below));
		if(isSignal != shouldSignal) ext.setBlockState(state.setValue(BlockStateProperties.SIGNAL_FIRE, shouldSignal));
		
		if(state.getValue(BlockStateProperties.LIT)) {
			if(boat.isUnderWater())	{
				dowse(boat, ext, state);
			} else if(boat.level.isClientSide) {
				if(((AccessorCampfireBlock) cb).bwe$spawnLavaParticles() && boat.level.random.nextFloat() < 0.08f) { //crude randomDisplayTick/5 estimation
					Vec3 p = BoatRules.positionOfBlock(boat);
					for (int i = 0; i < boat.level.random.nextInt(1) + 1; ++i) {
						boat.level.addParticle(ParticleTypes.LAVA,
							p.x, p.y + 0.5, p.z,
							boat.level.random.nextFloat() / 2f, 0.00005, boat.level.random.nextFloat() / 2f
						);
					}
				}
				
				if(boat.level.random.nextFloat() < 0.11f) {
					for(int i = 0; i < boat.level.random.nextInt(2) + 2; i++) {
						particle(boat, ext, state, false);
					}
				}
			}
		}
	}
}
