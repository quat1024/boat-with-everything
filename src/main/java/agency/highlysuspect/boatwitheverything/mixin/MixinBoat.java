package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Boat.class)
public class MixinBoat {
	// synched data and other setup //
	
	@Unique private static final EntityDataAccessor<Optional<BlockState>> DATA_ID_BLOCK_STATE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BLOCK_STATE);
	@Unique private static final EntityDataAccessor<ItemStack> DATA_ID_ITEM_STACK = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.ITEM_STACK);
	
	static {
		BoatWithEverything.DATA_ID_BLOCK_STATE = DATA_ID_BLOCK_STATE;
		BoatWithEverything.DATA_ID_ITEM_STACK = DATA_ID_ITEM_STACK;
	}
	
	@Inject(method = "defineSynchedData", at = @At("RETURN"))
	protected void whenDefiningSynchedData(CallbackInfo ci) {
		boat().getEntityData().define(DATA_ID_BLOCK_STATE, Optional.empty());
		boat().getEntityData().define(DATA_ID_ITEM_STACK, ItemStack.EMPTY);
	}
	
	// interactions //
	
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void whenInteracting(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = BoatWithEverything.interact(boat(), player, hand);
		if(result != null) cir.setReturnValue(result);
	}
	
	// saving and loading //
	
	@Unique private static final String BLOCKSTATE_KEY = "BoatWithEverything$blockState";
	@Unique private static final String ITEMSTACK_KEY = "BoatWithEverything$itemStack";
	
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void whenAddingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		boat().getEntityData().get(DATA_ID_BLOCK_STATE).ifPresent(state -> tag.put(BLOCKSTATE_KEY, NbtUtils.writeBlockState(state)));
		
		ItemStack stack = boat().getEntityData().get(DATA_ID_ITEM_STACK);
		if(!stack.isEmpty()) tag.put(ITEMSTACK_KEY, stack.save(new CompoundTag()));
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void whenReadingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		if(tag.contains(BLOCKSTATE_KEY) && !NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_KEY)).isAir()) {
			boat().getEntityData().set(DATA_ID_BLOCK_STATE, Optional.of(NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_KEY))));
		} else {
			boat().getEntityData().set(DATA_ID_BLOCK_STATE, Optional.empty());
		}
		
		if(tag.contains(ITEMSTACK_KEY)) {
			boat().getEntityData().set(DATA_ID_ITEM_STACK, ItemStack.of(tag.getCompound(ITEMSTACK_KEY)));
		} else {
			boat().getEntityData().set(DATA_ID_ITEM_STACK, ItemStack.EMPTY);
		}
	}
	
	// passenger positioning tweaks //
	
	@Inject(method = "getMaxPassengers", at = @At("HEAD"), cancellable = true)
	protected void whenCountingMaxPassengers(CallbackInfoReturnable<Integer> cir) {
		if(boat().getEntityData().get(BoatWithEverything.DATA_ID_BLOCK_STATE).isPresent()) cir.setReturnValue(1);
	}
	
	@Inject(method = "getSinglePassengerXOffset", at = @At("HEAD"), cancellable = true)
	protected void whenOffsettingSinglePassenger(CallbackInfoReturnable<Float> cir) {
		if(boat().getEntityData().get(BoatWithEverything.DATA_ID_BLOCK_STATE).isPresent()) cir.setReturnValue(0.15f); //same as ChestBoat
	}
	
	// helper //
	
	@Unique
	private Boat boat() {
		return (Boat) (Object) this;
	}
}
