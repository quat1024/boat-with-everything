package agency.highlysuspect.boatwitheverything;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BoatWithEverything {
	//Instantiated as a static final field in Boat (to make sure it happens at a
	//"predictable" time, cause entity data accessors are a bit weird), then
	//squirreled away into this field so I can reach it from here. If you NPE on
	//this it means noone has classloaded Boat yet. I mean, it's a vanilla entity,
	//so if that happens you will probably not go to space today
	public static EntityDataAccessor<Optional<BlockState>> DATA_ID_BLOCK_STATE;
	
	public static @Nullable InteractionResult interact(Boat boat, Player player, InteractionHand hand) {
		//Vanilla boat interaction always instantly returns when you're sneaking, so adding more behavior on sneak doesn't conflict
		if(!player.isSecondaryUseActive()) return null;
		
		//If there's something in the boat already, pop it out
		if(hasBlockState(boat)) {
			//return the item that was used to place the block in the boat
			ItemStack placementItem = ((BoatDuck) boat).boatWithEverything$getItemStack();
			((BoatDuck) boat).boatWithEverything$setItemStack(ItemStack.EMPTY);
			boat.spawnAtLocation(placementItem); //idk
			
			//remove the blockstate from the boat
			setBlockState(boat, null);
			
			return InteractionResult.SUCCESS;
		}
		
		//If there's no blockstate, add it to the boat
		BlockState placement;
		if(canAddBlockState(boat) && (placement = getPlacementStateInsideBoat(player.getItemInHand(hand))) != null) {
			setBlockState(boat, placement);
			((BoatDuck) boat).boatWithEverything$setItemStack(player.getItemInHand(hand).split(1));
			
			return InteractionResult.SUCCESS;
		}
		
		return null;
	}
	
	public static @Nullable BlockState getPlacementStateInsideBoat(ItemStack stack) {
		//Big TODO
		if(stack.getItem() instanceof BlockItem bi) return bi.getBlock().defaultBlockState();
		else return null;
	}
	
	public static boolean canAddBlockState(Boat boat) {
		return !(boat instanceof ChestBoat) && boat.getPassengers().size() <= 1 && !hasBlockState(boat);
	}
	
	///
	
	public static boolean hasBlockState(Boat boat) {
		return getBlockState(boat).isPresent();
	}
	
	public static Optional<BlockState> getBlockState(Boat boat) {
		return boat.getEntityData().get(DATA_ID_BLOCK_STATE);
	}
	
	public static void setBlockState(Boat boat, @Nullable BlockState state) {
		boat.getEntityData().set(DATA_ID_BLOCK_STATE, Optional.ofNullable(state));
	}
}
