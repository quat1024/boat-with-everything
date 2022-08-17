package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.mixin.AccessorHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.Level;
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
			
			int slot = getRandomSlot(boat.level.getRandom());
			ItemStack stackInSlot = getItem(slot); //safely handles the -1 case
			if(stackInSlot.isEmpty()) return;
			
			//First, if there is a container, try putting the item in the container
			Vec3 dropperPos = SpecialBoatRules.positionOfBlock(boat).add(0, 0.5, 0); //cause it returns the bottom-center
			Vec3 dropperNormal;
			if(vertical) dropperNormal = new Vec3(0, facing.getStepY(), 0);
			else {
				float dropperYRot = (boat.getYRot() + facing.toYRot()) * Mth.DEG_TO_RAD;
				dropperNormal = new Vec3(Mth.cos(dropperYRot), 0, Mth.sin(dropperYRot));
			}
			
			ItemStack leftover;
			Container container = getContainerAtButNotTheBoatLmao(boat.level, dropperPos.add(dropperNormal));
			if(container != null) {
				leftover = HopperBlockEntity.addItem(this, container, stackInSlot.copy().split(1), facing.getOpposite());
				//copied from dropper code, don't ask me!
				if(leftover.isEmpty()) {
					leftover = stackInSlot.copy();
					leftover.shrink(1);
				} else leftover = stackInSlot.copy();
			} else {
				//figure out what item entity to drop
				ItemStack toDrop = stackInSlot.split(1);
				leftover = stackInSlot;
				//now looking at DefaultDispenseItemBehavior
				Vec3 itemStartPos = dropperPos.add(dropperNormal.scale(1)).add(0, vertical ? -0.125 : -0.15625, 0); //hey, wasn't my idea
				ItemEntity ent = new ItemEntity(boat.level, itemStartPos.x, itemStartPos.y, itemStartPos.z, toDrop);
				
				//figure out its velocity
				double g = boat.level.getRandom().nextDouble() * 0.1 + 0.2;
				double spread = 0.103365; //0.0172275 * 6
				Vec3 dropperTangent = new Vec3(vertical ? 1 : 0, vertical ? 0 : 1, 0);
				Vec3 dropperBitangent = dropperNormal.cross(dropperTangent);
				Vec3 result =
					dropperNormal.scale(boat.level.getRandom().triangle(g, spread)).add(
						dropperTangent.scale(boat.level.getRandom().triangle(0.2, spread)).add(
							dropperBitangent.scale(boat.level.getRandom().triangle(g, spread))
						)
					);
				ent.setDeltaMovement(result.x, result.y, result.z);
				boat.level.addFreshEntity(ent);
				//itemEntity.setDeltaMovement(
				// level.random.triangle((double)direction.getStepX() * g, 0.0172275 * 6),
				// level.random.triangle(0.2, 0.0172275 * 6),
				// level.random.triangle((double)direction.getStepZ() * g, 0.0172275 * 6));
			}
			
			setItem(slot, leftover);
		}
		
		public Container getContainerAtButNotTheBoatLmao(Level level, Vec3 pos){
			try {
				BoatWithEverything.HOPPER_SKIP_THIS_BOAT_PLEASE.set(boat);
				return AccessorHopperBlockEntity.getContainerAt(level, pos.x, pos.y, pos.z);
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
	}
}
