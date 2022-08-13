package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
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
public class MixinBoat implements BoatDuck {
	// synched data and other setup //
	
	@Unique private static final EntityDataAccessor<Optional<BlockState>> DATA_ID_BLOCK_STATE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BLOCK_STATE);
	
	static {
		BoatWithEverything.DATA_ID_BLOCK_STATE = DATA_ID_BLOCK_STATE;
	}
	
	@Inject(method = "defineSynchedData", at = @At("RETURN"))
	protected void whenDefiningSynchedData(CallbackInfo ci) {
		boat().getEntityData().define(DATA_ID_BLOCK_STATE, Optional.empty());
	}
	
	// interactions //
	
	@Unique private ItemStack itemStack = ItemStack.EMPTY;
	
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void whenInteracting(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = BoatWithEverything.interact(boat(), player, hand);
		if(result != null) cir.setReturnValue(result);
	}
	
	@Override
	public void boatWithEverything$setItemStack(ItemStack stack) {
		this.itemStack = stack.copy();
	}
	
	@Override
	public ItemStack boatWithEverything$getItemStack() {
		return itemStack;
	}
	
	// saving and loading //
	
	@Unique private static final String BLOCKSTATE_KEY = "BoatWithEverything$blockState";
	@Unique private static final String ITEMSTACK_KEY = "BoatWithEverything$itemStack";
	
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void whenAddingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		BoatWithEverything.getBlockState(boat()).ifPresent(state ->
			tag.put(BLOCKSTATE_KEY, NbtUtils.writeBlockState(state)));
		
		if(!itemStack.isEmpty()) tag.put(ITEMSTACK_KEY, itemStack.save(new CompoundTag()));
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void whenReadingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		BlockState state;
		BoatWithEverything.setBlockState(boat(),
			tag.contains(BLOCKSTATE_KEY) && !(state = NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_KEY))).isAir() ? state : null);
		
		boatWithEverything$setItemStack(
			tag.contains(ITEMSTACK_KEY) ? ItemStack.of(tag.getCompound(ITEMSTACK_KEY)) : ItemStack.EMPTY);
	}
	
	// passenger positioning tweaks //
	
	@Inject(method = "getMaxPassengers", at = @At("HEAD"), cancellable = true)
	protected void whenCountingMaxPassengers(CallbackInfoReturnable<Integer> cir) {
		if(BoatWithEverything.hasBlockState(boat())) cir.setReturnValue(1);
	}
	
	@Inject(method = "getSinglePassengerXOffset", at = @At("HEAD"), cancellable = true)
	protected void whenOffsettingSinglePassenger(CallbackInfoReturnable<Float> cir) {
		if(BoatWithEverything.hasBlockState(boat())) cir.setReturnValue(0.15f); //same as ChestBoat
	}
	
	// helper //
	
	@Unique
	private Boat boat() {
		return (Boat) (Object) this;
	}
}
