package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(Boat.class)
public abstract class MixinBoat extends Entity implements BoatDuck {
	// mixin gunk //
	
	public MixinBoat(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}
	
	// synched data //
	
	@Unique private static final EntityDataAccessor<Optional<BlockState>> DATA_ID_BLOCK_STATE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BLOCK_STATE);
	@Unique private static final EntityDataAccessor<ItemStack> DATA_ID_ITEM_STACK = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.ITEM_STACK);
	@Unique private SpecialBoatRules rules;
	
	@Inject(method = "defineSynchedData", at = @At("RETURN"))
	protected void whenDefiningSynchedData(CallbackInfo ci) {
		boat().getEntityData().define(DATA_ID_BLOCK_STATE, Optional.empty());
		boat().getEntityData().define(DATA_ID_ITEM_STACK, ItemStack.EMPTY);
	}
	
	@Unique private final BoatExt ext = new BoatExt() {
		@Override
		public @Nullable BlockState getBlockState() {
			return boat().getEntityData().get(DATA_ID_BLOCK_STATE).orElse(null);
		}
		
		@Override
		public void setBlockState(@Nullable BlockState state) {
			boat().getEntityData().set(DATA_ID_BLOCK_STATE, Optional.ofNullable(state));
			
			if(state == null) rules = null;
			else rules = SpecialBoatRules.get(state);
		}
		
		@Override
		public @NotNull ItemStack getItemStack() {
			return boat().getEntityData().get(DATA_ID_ITEM_STACK);
		}
		
		@Override
		public void setItemStack(@NotNull ItemStack stack) {
			boat().getEntityData().set(DATA_ID_ITEM_STACK, stack);
		}
		
		@Override
		public SpecialBoatRules getRules() {
			return rules;
		}
		
		@Override
		public int getMaxPassengers() {
			return MixinBoat.this.getMaxPassengers();
		}
		
		@Override
		public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
			//Called from MixinEntity upwards in the hierarchy - BoatEntity doesn't override onSyncedDataUpdated itself, so there's nothing to inject to.
			//We need to make sure that when vanilla uses the EntityDataAccessor system, clients know to change the cached SpecialBoatRules instance as well.
			if(accessor == DATA_ID_BLOCK_STATE) {
				BlockState state = boat().getEntityData().get(DATA_ID_BLOCK_STATE).orElse(null);
				
				if(state == null) rules = null;
				else rules = SpecialBoatRules.get(state);
			}
		}
	};
	
	// interactions //
	
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void whenInteracting(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = BoatWithEverything.interact(boat(), ext, player, hand);
		if(result != InteractionResult.PASS) cir.setReturnValue(result);
	}
	
	@Inject(method = "hurt", at = @At(
		//After the isClientSide check, but before effects such as damage animations begin to happen.
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/vehicle/Boat;setHurtDir(I)V"
	), cancellable = true)
	public void whenHurting(DamageSource src, float amount, CallbackInfoReturnable<Boolean> cir) {
		if(BoatWithEverything.hurt(boat(), ext, src)) cir.setReturnValue(false);
	}
	
	@Inject(method = "destroy", at = @At("RETURN"))
	public void whenDestroying(DamageSource source, CallbackInfo ci) {
		boat().spawnAtLocation(ext.getItemStack());
		ext.clearBlockState();
		ext.clearItemStack();
	}
	
	@Inject(method = "checkFallDamage", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/vehicle/Boat;kill()V"
	))
	public void whenDoingGlitchyPlanksAndSticksDropLol(double d, boolean bl, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
		//Could avoid this gamerule check with a more annoying mixin inject, I guess
		if(boat().level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			boat().spawnAtLocation(ext.getItemStack());
			ext.clearBlockState();
			ext.clearItemStack();
		}
	}
	
	@Inject(method = "tick", at = @At("RETURN"))
	public void whenTicking(CallbackInfo ci) {
		SpecialBoatRules rules = ext.getRules();
		if(rules != null) rules.tick(boat(), ext);
	}
	
	// saving and loading //
	
	@Unique private static final String BLOCKSTATE_KEY = "BoatWithEverything$blockState";
	@Unique private static final String ITEMSTACK_KEY = "BoatWithEverything$itemStack";
	
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void whenAddingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		BlockState state = ext.getBlockState();
		if(state != null) tag.put(BLOCKSTATE_KEY, NbtUtils.writeBlockState(state));
		
		ItemStack stack = ext.getItemStack();
		if(!stack.isEmpty()) tag.put(ITEMSTACK_KEY, stack.save(new CompoundTag()));
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void whenReadingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		if(tag.contains(BLOCKSTATE_KEY) && !NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_KEY)).isAir()) {
			ext.setBlockState(NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_KEY)));
		} else {
			ext.clearBlockState();
		}
		
		if(tag.contains(ITEMSTACK_KEY)) {
			ext.setItemStack(ItemStack.of(tag.getCompound(ITEMSTACK_KEY)));
		} else {
			ext.clearItemStack();
		}
	}
	
	// passenger tweaks //
	
	@Inject(method = "getMaxPassengers", at = @At("HEAD"), cancellable = true)
	protected void whenCountingMaxPassengers(CallbackInfoReturnable<Integer> cir) {
		SpecialBoatRules rules = ext.getRules();
		if(rules != null && rules.consumesPassengerSlot()) cir.setReturnValue(1);
	}
	
	@Inject(method = "getSinglePassengerXOffset", at = @At("HEAD"), cancellable = true)
	protected void whenOffsettingSinglePassenger(CallbackInfoReturnable<Float> cir) {
		SpecialBoatRules rules = ext.getRules();
		if(rules != null && rules.consumesPassengerSlot()) cir.setReturnValue(0.15f); //same as ChestBoat
	}
	
	// helper //
	
	@Unique private Boat boat() {
		return (Boat) (Object) this;
	}
	
	@Override
	public BoatExt bwe$getExt() {
		return ext;
	}
	
	@Shadow protected abstract int getMaxPassengers();
}
