package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.menu.Menus;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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
	public static final String MODID = "boat-with-everything";
	public static BoatWithEverything INSTANCE;
	
	public BoatWithEverything(LoaderServices services) {
		Menus.register(services);
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static boolean hurt(Boat boat, BoatExt ext, DamageSource source) {
		if(!ext.hasBlockState()) return false;
		
		//return the item that was used to place the block in the boat
		ItemStack stackInBoat = ext.getItemStack().copy();
		@Nullable Player player = source.getEntity() instanceof Player p ? p : null;
		if(player == null || !player.addItem(stackInBoat)) {
			boat.spawnAtLocation(stackInBoat, boat.getBbHeight());
		}
		
		//remove all the stuff from the boat
		ext.clearBlockState();
		ext.clearItemStack();
		
		boat.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM); //todo caption
		return player != null; //Only cancel vanilla damage handling if it was from a player punch.
	}
	
	public static @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {		
		//Vanilla boat interaction always instantly returns when you're sneaking, so adding more behavior on sneak doesn't conflict
		if(!player.isSecondaryUseActive()) return InteractionResult.PASS;
		
		//If there's something in the boat already, perform its right click action
		@Nullable SpecialBoatRules rules = ext.getRules();
		if(rules != null && hand == InteractionHand.MAIN_HAND) {
			InteractionResult result = rules.interact(boat, ext, player, hand);
			if(result != InteractionResult.PASS) return result;
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
