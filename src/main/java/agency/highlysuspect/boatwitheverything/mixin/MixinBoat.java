package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.HackyEntityUpdateIds;
import agency.highlysuspect.boatwitheverything.RenderData;
import agency.highlysuspect.boatwitheverything.block.BoatLightBlock;
import agency.highlysuspect.boatwitheverything.block.BoatLightBlockEntity;
import agency.highlysuspect.boatwitheverything.container.ContainerExt;
import agency.highlysuspect.boatwitheverything.special.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.special.SpecialChestRules;
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
import net.minecraft.world.level.block.Blocks;
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

import java.util.Objects;
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
	//For mapmakers, conventions, etc
	@Unique private static final EntityDataAccessor<Boolean> DATA_ID_LOCKED = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.BOOLEAN);
	
	@Unique private @Nullable SpecialBoatRules rules;
	@Unique private @Nullable ContainerExt container;
	
	@Unique private RenderData renderAttachmentData; //SpecialBoatRenderers can squirrel away data that persists from frame-to-frame here
	
	//set from the specialboatrules when it is carrying a heavy object
	@Unique private boolean heavy = false;
	
	//timer that's set when you fill the boat with water, forcing it to sink
	//only set for a couple seconds b/c once the boat passes the surface for real, vanilla "boat underwater" logic will take over
	@Unique private int forceSink = 0;
	
	@Inject(method = "defineSynchedData", at = @At("RETURN"))
	protected void whenDefiningSynchedData(CallbackInfo ci) {
		boat().getEntityData().define(DATA_ID_BLOCK_STATE, Optional.empty());
		boat().getEntityData().define(DATA_ID_ITEM_STACK, ItemStack.EMPTY);
		boat().getEntityData().define(DATA_ID_LOCKED, false);
	}
	
	@Unique private final BoatExt ext = new BoatExt() {
		@Override
		public @Nullable BlockState getBlockState() {
			return boat().getEntityData().get(DATA_ID_BLOCK_STATE).orElse(null);
		}
		
		@Override
		public void setBlockState(@Nullable BlockState state) {
			boat().getEntityData().set(DATA_ID_BLOCK_STATE, Optional.ofNullable(state));
			setBlockState0(state);
		}
		
		private void setBlockState0(@Nullable BlockState state) {
			if(state == null) {
				if(container != null) container.drop(boat(), this);
				
				rules = null;
				container = null;
				renderAttachmentData = null;
			} else {
				rules = BoatWithEverything.INSTANCE.rulesRegistry.get().get(state);
				
				//only swap out the container if a different implementation was returned w/ the new blockstate
				//makes it so stuff like opening the barrel doesn't delete the container because the blockstate changed
				//Hey cheat client developers this is probably where you should look to find the egregious dupe bugs in the mod
				ContainerExt newContainer = rules.makeNewContainer(boat(), this);
				Class<?> oldContainerClass = container == null ? null : container.getClass();
				Class<?> newContainerClass = newContainer == null ? null : newContainer.getClass();
				if(!Objects.equals(oldContainerClass, newContainerClass)) {
					if(container != null) container.drop(boat(), this);
					container = newContainer;
					renderAttachmentData = null;
				}
				
				//Wow! It's Bad:tm:
				if(level.isClientSide && (state.is(Blocks.CHEST) || state.is(Blocks.ENDER_CHEST))) setRenderAttachmentData(new SpecialChestRules.ChestLidRenderData());
			}
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
		public @Nullable ContainerExt getContainer() {
			return container;
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
			//Called from MixinEntity, upwards in the hierarchy - BoatEntity doesn't override onSyncedDataUpdated itself, so there's nothing to inject to.
			//We need to make sure that when vanilla uses the EntityDataAccessor system, clients know to change the cached SpecialBoatRules instance as well.
			if(DATA_ID_BLOCK_STATE.equals(accessor)) {
				BlockState state = boat().getEntityData().get(DATA_ID_BLOCK_STATE).orElse(null);
				setBlockState0(state);
			}
		}
		
		@Override
		public RenderData getRenderAttachmentData() {
			return renderAttachmentData;
		}
		
		@Override
		public void setRenderAttachmentData(RenderData whatever) {
			renderAttachmentData = whatever;
		}
		
		@Override
		public boolean isLocked() {
			return boat().getEntityData().get(DATA_ID_LOCKED);
		}
		
		@Override
		public void clickWithWaterBucket() {
			setForceSink();
			level.broadcastEntityEvent(boat(), HackyEntityUpdateIds.FILL_BOAT_WITH_WATER_LOL); //so other clients get it
		}
		
		@Override
		public void setForceSink() {
			forceSink = 40;
		}
	};
	
	// interactions //
	
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void whenInteracting(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = BoatWithEverything.INSTANCE.interact(boat(), ext, player, hand);
		if(result != InteractionResult.PASS) cir.setReturnValue(result);
	}
	
	@Inject(method = "hurt", at = @At(
		//After the isClientSide check, but before effects such as damage animations begin to happen.
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/vehicle/Boat;setHurtDir(I)V"
	), cancellable = true)
	public void whenHurting(DamageSource src, float amount, CallbackInfoReturnable<Boolean> cir) {
		if(BoatWithEverything.INSTANCE.hurt(boat(), ext, src)) cir.setReturnValue(false);
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
	
	@Shadow private Boat.Status status;
	@Shadow private Boat.Status oldStatus;
	
	@Inject(method = "tick", at = @At("RETURN"))
	public void whenTicking(CallbackInfo ci) {
		SpecialBoatRules rules = ext.getRules();
		if(rules != null) {
			heavy = rules.isHeavy();
			rules.tick(boat(), ext);
			
			//TODO find a better home for this maybe lmao
			int light = rules.light(boat(), ext);
			if(light != 0) {
				BlockPos lightBlockPos = BoatLightBlockEntity.lightBlockPos(boat());
				BlockState stateThere = level.getBlockState(lightBlockPos);
				if(stateThere.isAir() && !(stateThere.getBlock() instanceof BoatLightBlock)) {
					level.setBlockAndUpdate(BoatLightBlockEntity.lightBlockPos(boat()), BoatWithEverything.INSTANCE.boatLightBlock.withLevel(light));
				}
			}
		} else {
			heavy = false;
		}
		
		if(forceSink > 0) forceSink--;
		
		if(boat().level.isClientSide && renderAttachmentData != null) renderAttachmentData.tick(boat(), ext);
	}
	
	//force the boat underwater if it is carrying a heavy object
	@Inject(method = "floatBoat", at = @At("HEAD"))
	private void whenFloating(CallbackInfo ci) {
		//setting the status at the start of this method influences which way this code will push the boat
		if(heavy || forceSink > 0) {
			status = oldStatus = Boat.Status.UNDER_WATER;
		}
	}
	
	@Inject(method = "floatBoat", at = @At("RETURN"))
	private void ohComeOnItNeedsToFallFasterThanThat(CallbackInfo ci) {
		if(heavy && getDeltaMovement().y < 0) {
			setDeltaMovement(getDeltaMovement().add(0, -0.15f, 0));
		}
	}
	
	//BoatRenderer checks isUnderWater when doing rendering, if it's true it doesn't draw the water-mask rect.
	//This method is not to be confused with "isUnderwater", which actually checks for water blocks above the boat lmao
	@Inject(method = "isUnderWater", at = @At("HEAD"), cancellable = true)
	private void whenCheckingUnderWater(CallbackInfoReturnable<Boolean> cir) {
		if(forceSink > 0) cir.setReturnValue(true);
	}
	
	// saving and loading //
	
	@Unique private static final String BLOCKSTATE_KEY = "BoatWithEverything$blockState";
	@Unique private static final String ITEMSTACK_KEY = "BoatWithEverything$itemStack";
	@Unique private static final String LOCKED_KEY = "BoatWithEverything$locked";
	
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void whenAddingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		BlockState state = ext.getBlockState();
		if(state != null) tag.put(BLOCKSTATE_KEY, NbtUtils.writeBlockState(state));
		
		ItemStack stack = ext.getItemStack();
		if(!stack.isEmpty()) tag.put(ITEMSTACK_KEY, stack.save(new CompoundTag()));
		
		SpecialBoatRules rules = ext.getRules();
		if(rules != null) rules.addAdditionalSaveData(boat(), ext, tag);
		
		tag.putBoolean(LOCKED_KEY, boat().getEntityData().get(DATA_ID_LOCKED));
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
		
		SpecialBoatRules rules = ext.getRules();
		if(rules != null) rules.readAdditionalSaveData(boat(), ext, tag);
		
		boat().getEntityData().set(DATA_ID_LOCKED, tag.getBoolean(LOCKED_KEY));
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
