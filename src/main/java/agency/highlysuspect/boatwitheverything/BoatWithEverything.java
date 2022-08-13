package agency.highlysuspect.boatwitheverything;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BoatWithEverything {
	//Instantiated as static final fields in Boat (to make sure it happens at a "predictable" time, cause entity data accessors
	//are a bit weird), then squirreled away into this field so I can reach it from here. If you NPE on these it means noone has
	//classloaded Boat yet. I mean, it's a vanilla entity, so if that happens you will probably not go to space today.
	public static EntityDataAccessor<Optional<BlockState>> DATA_ID_BLOCK_STATE;
	public static EntityDataAccessor<ItemStack> DATA_ID_ITEM_STACK;
	
	public static @Nullable InteractionResult interact(Boat boat, Player player, InteractionHand hand) {
		//Vanilla boat interaction always instantly returns when you're sneaking, so adding more behavior on sneak doesn't conflict
		if(!player.isSecondaryUseActive()) return null;
		
		//If there's something in the boat already, pop it out
		if(boat.getEntityData().get(DATA_ID_BLOCK_STATE).isPresent()) {
			//return the item that was used to place the block in the boat
			ItemStack placementItem = boat.getEntityData().get(DATA_ID_ITEM_STACK).copy();
			boat.getEntityData().set(DATA_ID_ITEM_STACK, ItemStack.EMPTY);
			boat.spawnAtLocation(placementItem, boat.getBbHeight());
			
			//remove the blockstate from the boat
			boat.getEntityData().set(DATA_ID_BLOCK_STATE, Optional.empty());
			
			boat.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM); //todo caption
			
			return InteractionResult.SUCCESS;
		}
		
		//If there's no blockstate, add it to the boat
		BlockState placement;
		if(canAddBlockState(boat) && (placement = getPlacementStateInsideBoat(player, boat, hand)) != null) {
			boat.getEntityData().set(DATA_ID_BLOCK_STATE, Optional.of(placement));
			boat.getEntityData().set(DATA_ID_ITEM_STACK, player.getItemInHand(hand).split(1));
			
			boat.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM); //todo caption
			
			return InteractionResult.SUCCESS;
		}
		
		return null;
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
	
	public static boolean canAddBlockState(Boat boat) {
		return !(boat instanceof ChestBoat) && boat.getPassengers().size() <= 1 && boat.getEntityData().get(DATA_ID_BLOCK_STATE).isEmpty();
	}
}
