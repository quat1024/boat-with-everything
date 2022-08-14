package agency.highlysuspect.boatwitheverything;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoatWithEverything {	
	public static @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {		
		//Vanilla boat interaction always instantly returns when you're sneaking, so adding more behavior on sneak doesn't conflict
		if(!player.isSecondaryUseActive()) return InteractionResult.PASS;
		
		//If there's something in the boat already, pop it out
		@Nullable BlockState state = ext.getBlockState();
		if(state != null) {
			//If there's an rclick interaction don't do that though
			//Idk just break the boat if you want the block back
			//TODO maybe move removing the item to punching the boat
			SpecialBoatRules rules = ext.getRules();
			if(rules != null) {
				InteractionResult result = ext.getRules().interact(boat, ext);
				if(result != InteractionResult.PASS) return result;
			}
			
			//return the item that was used to place the block in the boat
			ItemStack stackInBoat = ext.getItemStack().copy();
			if(!player.addItem(stackInBoat)) boat.spawnAtLocation(stackInBoat, boat.getBbHeight());
			
			//remove all the stuff from the boat
			ext.clearBlockState();
			ext.clearItemStack();
			
			boat.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM); //todo caption
			
			return InteractionResult.SUCCESS;
		}
		
		//If there's no blockstate, add it to the boat
		BlockState placementState;
		if((placementState = getPlacementStateInsideBoat(player, boat, hand)) != null && canAddBlockState(boat, ext, placementState)) {
			ext.setBlockState(placementState);
			ext.setItemStack(player.getItemInHand(hand).split(1));
			
			boat.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM); //todo caption
			
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	public static @Nullable BlockState getPlacementStateInsideBoat(Player player, Boat boat, InteractionHand hand) {
		//Big TODO
		ItemStack stack = player.getItemInHand(hand);
		if(stack.getItem() instanceof BlockItem bi) {
			//Turn the player momentarily to fool anything using BlockPlaceContext#getDirection or similar
			float oldYRot = player.getYRot();
			float oldYHeadRot = player.getYHeadRot();
			float oldYRot0 = player.yRotO;
			
			float relativeDirection = Mth.wrapDegrees(player.getYRot() - boat.getYRot());
			player.setYRot(relativeDirection); //used by BlockPlaceContext#getDirection
			player.setYHeadRot(relativeDirection); //used by #getNearestLookingDirection but only on the server lol
			player.yRotO = relativeDirection; //idk cant hurt ?
			
			BlockState state = bi.getBlock().getStateForPlacement(new BlockPlaceContext(
				player, hand, stack, 
				new BlockHitResult(boat.position(), Direction.UP, boat.blockPosition(), true)
			));
			
			//restore player position
			player.setYRot(oldYRot);
			player.setYHeadRot(oldYHeadRot);
			player.yRotO = oldYRot0;
			
			//return
			return state; 
		} else return null;
	}
	
	public static boolean canAddBlockState(Boat boat, BoatExt ext, BlockState state) {
		if(boat instanceof ChestBoat || ext.hasBlockState()) return false;
		return boat.getPassengers().size() < ext.getMaxPassengers();
	}
}
