package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.cosmetic.ChestLidRenderData;
import agency.highlysuspect.boatwitheverything.cosmetic.RenderData;
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
	
	@Unique private @Nullable SpecialBoatRules rules;
	@Unique private @Nullable ContainerExt container;
	
	@Unique private RenderData renderAttachmentData; //SpecialBoatRenderers can squirrel away data that persists from frame-to-frame here
	
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
			setBlockState0(state);
		}
		
		private void setBlockState0(@Nullable BlockState state) {
			if(state == null) {
				if(container != null) container.drop(boat(), this);
				
				rules = null;
				container = null;
				renderAttachmentData = null;
			} else {
				rules = SpecialBoatRules.get(state);
				
				//Wow! It's Bad:tm:
				if(level.isClientSide && (state.is(Blocks.CHEST) || state.is(Blocks.ENDER_CHEST))) setRenderAttachmentData(new ChestLidRenderData());
				
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
			if(accessor == DATA_ID_BLOCK_STATE) {
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
		
		if(boat().level.isClientSide && renderAttachmentData != null) renderAttachmentData.tick(boat(), ext);
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
		
		SpecialBoatRules rules = ext.getRules();
		if(rules != null) rules.addAdditionalSaveData(boat(), ext, tag);
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
