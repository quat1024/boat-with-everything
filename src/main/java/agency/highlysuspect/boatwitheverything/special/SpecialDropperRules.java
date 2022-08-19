package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.mixin.AccessorHopperBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SpecialDropperRules implements SpecialBoatRules {
	@Override
	public @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		return new DropperContainerExt(boat, ext);
	}
	
	@Override
	public void tick(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		if(state == null || !state.hasProperty(BlockStateProperties.TRIGGERED)) return;
		
		boolean isPowered = state.getValue(BlockStateProperties.TRIGGERED);
		boolean shouldPower = SpecialBoatRules.isPowered(boat);
		if(isPowered != shouldPower) {
			ext.setBlockState(state.setValue(BlockStateProperties.TRIGGERED, shouldPower));
			if(shouldPower && ext.getContainer() instanceof DropperContainerExt e) {
				e.drop();
			}
		}
	}
	
	public static class DropperContainerExt extends ContainerExt.SimpleContainerImpl implements ContainerExt {
		public DropperContainerExt(Boat boat, BoatExt ext) {
			super(boat, ext, 9);
		}
		
		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int sequence, Inventory playerInventory, Player player) {
			return new DispenserMenu(sequence, playerInventory, this);
		}
		
		public void drop() {
			if(boat.level.isClientSide) return;
			
			BlockState state = ext.getBlockState();
			if(state == null || !state.hasProperty(BlockStateProperties.FACING)) return;
			Direction facing = state.getValue(BlockStateProperties.FACING);
			boolean vertical = facing.getAxis() == Direction.Axis.Y;
			
			//Position of dropper
			Vec3 dropperPos = SpecialBoatRules.positionOfBlock(boat).add(0, 0.5, 0); //cause it returns the bottom-center
			
			//Pick an item, any item
			int slot = getRandomSlot(boat.level.getRandom());
			ItemStack stackInSlot = getItem(slot); //safely handles the -1 case on getRandomSlot
			if(stackInSlot.isEmpty()) {
				boat.level.playSound(null, dropperPos.x, dropperPos.y, dropperPos.z, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1f, 1.2f);
				return;
			}
			
			//Compute normal, tangent, and bitangent vectors
			Vec3 dropperNormal;
			if(vertical) dropperNormal = new Vec3(0, facing.getStepY(), 0);
			else {
				float dropperYRot = (boat.getYRot() + facing.toYRot() + 90) * Mth.DEG_TO_RAD;
				dropperNormal = new Vec3(Mth.cos(dropperYRot), 0, Mth.sin(dropperYRot));
			}
			Vec3 dropperTangent = new Vec3(vertical ? 1 : 0, vertical ? 0 : 1, 0);
			Vec3 dropperBitangent = dropperNormal.cross(dropperTangent);
			
			//Behavior
			Behavior behavior = getBehavior(stackInSlot);
			if(behavior == null) return;
			
			ItemStack leftover = behavior.dispense(boat, ext, this, facing, stackInSlot, dropperPos, dropperNormal, dropperTangent, dropperBitangent);
			setItem(slot, leftover);
		}
		
		public Behavior getBehavior(ItemStack stack) {
			return Behavior.DROPPER;
		}
		
		public static Container getContainerAtButNotTheBoatLmao(Boat boat, Vec3 pos){
			try {
				BoatWithEverything.HOPPER_SKIP_THIS_BOAT_PLEASE.set(boat);
				return AccessorHopperBlockEntity.getContainerAt(boat.level, pos.x, pos.y, pos.z);
			} finally {
				BoatWithEverything.HOPPER_SKIP_THIS_BOAT_PLEASE.set(null);
			}
		}
		
		//Modified copy from DispenserBlockEntity. Look at this algorithm, isn't it neat?
		//Picks a random non-empty slot with uniform probability, in a single pass, with constant scratch space
		public int getRandomSlot(RandomSource randomSource) {
			int slot = -1, chance = 1;
			for (int i = 0; i < getContainerSize(); i++) {
				if (getItem(i).isEmpty() || randomSource.nextInt(chance++) != 0) continue;
				slot = i;
			}
			return slot;
		}
		
		public interface Behavior {
			ItemStack dispense(Boat boat, BoatExt ext, ContainerExt cont, Direction facing, ItemStack stackinSlot, Vec3 dropperPos, Vec3 dropperNormal, Vec3 dropperTangent, Vec3 dropperBitangent);
			
			static void dispenseSound(Boat boat, Vec3 dropperPos) {
				boat.level.playSound(null, dropperPos.x, dropperPos.y, dropperPos.z, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1f, 1f);
			}
			
			Behavior SPAWN_ITEM = (boat, ext, cont, facing, stackInSlot, dropperPos, dropperNormal, dropperTangent, dropperBitangent) -> {
				//figure out what item entity to drop
				ItemStack toDrop = stackInSlot.copy();
				toDrop.setCount(1);
				
				Vec3 itemStartPos = dropperPos.add(dropperNormal.scale(0.6));
				if(facing.getAxis() != Direction.Axis.Y) itemStartPos = itemStartPos.add(0, 0.3, 0); //Please stop glitching into the boat, thanks
				ItemEntity ent = new ItemEntity(boat.level, itemStartPos.x, itemStartPos.y, itemStartPos.z, toDrop);
				
				//Figure out its velocity.
				//This isn't quite "vanilla dropper code mathed to be non-axis-aligned", but it looks pretty okay?
				//I fudged the numbers until it looked similar to a vanilla dropper idk didnt think too hard. Its late
				double g = boat.level.getRandom().nextDouble() * 0.1 + 0.2;
				double spread = 0.103365; //0.0172275 * 6
				Vec3 result = dropperNormal.scale(boat.level.getRandom().triangle(g * 1.8, spread * 1.2));
				result = result.add(dropperTangent.scale(boat.level.getRandom().triangle(0, spread * 1.4)));
				result = result.add(dropperBitangent.scale(boat.level.getRandom().triangle(0, spread * 1.4)));
				
				ent.setDeltaMovement(result.x, result.y, result.z);
				ent.setPickUpDelay(10);
				boat.level.addFreshEntity(ent);
				dispenseSound(boat, dropperPos);
				
				ItemStack leftover = stackInSlot.copy();
				leftover.shrink(1);
				return leftover;
			};
			
			Behavior DROPPER = (boat, ext, cont, facing, stackInSlot, dropperPos, dropperNormal, dropperTangent, dropperBitangent) -> {
				Container container = getContainerAtButNotTheBoatLmao(boat, dropperPos.add(dropperNormal));
				if(container != null) {
					ItemStack leftover = HopperBlockEntity.addItem(cont, container, stackInSlot.copy().split(1), facing.getOpposite());
					//copied from dropper code, don't ask me!
					if(leftover.isEmpty()) {
						leftover = stackInSlot.copy();
						leftover.shrink(1);
					} else leftover = stackInSlot.copy();
					return leftover;
				}
				else return SPAWN_ITEM.dispense(boat, ext, cont, facing, stackInSlot, dropperPos, dropperNormal, dropperTangent, dropperBitangent);
			};
		}
	}
}
