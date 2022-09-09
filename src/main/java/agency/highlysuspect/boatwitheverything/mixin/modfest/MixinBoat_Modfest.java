package agency.highlysuspect.boatwitheverything.mixin.modfest;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ModfestHackery;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * i am extremely sorry for this code if it crashes or cause performance issues. If it doesn't im not sorry.
 */
@Mixin(value = Boat.class, priority = 950) //lower priority mixins are applied first, regular MixinBoat in this mod is 1000
public class MixinBoat_Modfest {
	
	/// GO DIRECTLY TO SYNCED DATA HELL, DO NOT PASS GO, DO NOT COLLECT $200 ///
	
	@Unique private static final EntityDataAccessor<Optional<Vec3>> FIXED_POS = SynchedEntityData.defineId(Boat.class, ModfestHackery.OPTIONAL_VEC3_SERIALIZER);
	@Unique private static final EntityDataAccessor<Float> FIXED_X_ROT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.FLOAT);
	@Unique private static final EntityDataAccessor<Float> FIXED_Y_ROT = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.FLOAT);
	
	@Inject(method = "defineSynchedData", at = @At("RETURN"))
	protected void whenDefiningSynchedData(CallbackInfo ci) {
		boat().getEntityData().define(FIXED_POS, Optional.empty());
		boat().getEntityData().define(FIXED_X_ROT, 0f);
		boat().getEntityData().define(FIXED_Y_ROT, 0f);
	}
	
	@Unique private boolean isFixed() {
		return boat().getEntityData().get(FIXED_POS).isPresent();
	}
	
	@SuppressWarnings("OptionalGetWithoutIsPresent") //check isFixed first
	@Unique private Vec3 getFixedPos() {
		return boat().getEntityData().get(FIXED_POS).get();
	}
	
	@Unique private float getFixedXRot() {
		return boat().getEntityData().get(FIXED_X_ROT);
	}
	
	@Unique private float getFixedYRot() {
		return boat().getEntityData().get(FIXED_Y_ROT);
	}
	
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Unique private void setAll(Optional<Vec3> fixedPos, float fixedXRot, float fixedYRot) {
		SynchedEntityData data = boat().getEntityData();
		data.set(FIXED_POS, fixedPos);
		data.set(FIXED_X_ROT, fixedXRot);
		data.set(FIXED_Y_ROT, fixedYRot);
	}
	
	/// INJECTOR HECK ///
	
	@Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
	private void boatwitheverything$modfest$onIsPushable(CallbackInfoReturnable<Boolean> cir) {
		if(isFixed()) cir.setReturnValue(false);
	}
	
	@Inject(method = "tick", at = @At("RETURN"))
	private void boatwitheverything$modfest$afterTick(CallbackInfo ci) {
		if(isFixed()) {
			boat().setPos(getFixedPos());
			boat().setXRot(getFixedXRot());
			boat().setYRot(getFixedYRot());
			boat().setDeltaMovement(Vec3.ZERO);
		}
	}
	
	@Inject(method = "push", at = @At("HEAD"), cancellable = true)
	private void boatwitheverything$modfest$onPush(Entity entity, CallbackInfo ci) {
		if(isFixed()) ci.cancel();
	}
	
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void boatwitheverything$modfest$onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if(player.isCreative() && hand == InteractionHand.MAIN_HAND && !player.level.isClientSide) {
			ItemStack held = player.getItemInHand(hand);
			if(held.getItem() == Items.FLINT) {
				if(isFixed()) {
					//unfix
					setAll(Optional.empty(), 0f, 0f);
					player.sendSystemMessage(Component.literal("unfixed boat"));
				} else {
					//fix
					setAll(Optional.of(boat().position()), boat().getXRot(), boat().getYRot());
					player.sendSystemMessage(Component.literal("fixed boat in place"));
				}
				cir.setReturnValue(InteractionResult.SUCCESS);
				return;
			}
			
			if(held.getItem() == Items.GOLDEN_APPLE) {
				boat().setInvulnerable(!boat().isInvulnerable());
				player.sendSystemMessage(Component.literal("boat is invulnerable: " + boat().isInvulnerable()));
				cir.setReturnValue(InteractionResult.SUCCESS);
				return;
			}
			
			if(held.getItem() == Items.IRON_INGOT) {
				ext().setLocked(!ext().isLocked());
				player.sendSystemMessage(Component.literal("boat item is locked: " + ext().isLocked()));
				cir.setReturnValue(InteractionResult.SUCCESS);
				return;
			}
		}
	}
	
	@Inject(method = "controlBoat", at = @At("HEAD"), cancellable = true)
	private void boatwitheverything$modfest$onControlBoat(CallbackInfo ci) {
		if(isFixed()) ci.cancel();
	}
	
	/// THE PART OF MAKING NEW FEATURES THATS ALWAYS SUPER FUN TO WRITE: ///
	
	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void boatwitheverything$modfest$onAddingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		if(isFixed()) {
			tag.put("bwe-modfest-fixedPos", vecToDoubleList(getFixedPos()));
			tag.putFloat("bwe-modfest-fixedXRot", getFixedXRot());
			tag.putFloat("bwe-modfest-fixedYRot", getFixedYRot());
		}
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void boatwitheverything$modfest$onReadingAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		boolean did = false;
		if(tag.contains("bwe-modfest-fixedPos")) {
			ListTag fixedPosList = tag.getList("bwe-modfest-fixedPos", 6);
			if(fixedPosList.size() == 3) {
				setAll(Optional.of(doubleListToVec(fixedPosList)), tag.getFloat("bwe-modfest-fixedXRot"), tag.getFloat("bwe-modfest-fixedYRot"));
				did = true;
			}
		}
		
		if(!did) setAll(Optional.empty(), 0f, 0f);
	}
	
	@Unique
	protected ListTag vecToDoubleList(Vec3 vec) {
		ListTag listTag = new ListTag();
		listTag.add(DoubleTag.valueOf(vec.x));
		listTag.add(DoubleTag.valueOf(vec.y));
		listTag.add(DoubleTag.valueOf(vec.z));
		return listTag;
	}
	
	@Unique protected Vec3 doubleListToVec(ListTag tag) {
		return new Vec3(tag.getDouble(0), tag.getDouble(1), tag.getDouble(2));
	}
	
	@Unique private Boat boat() {
		return (Boat) (Object) this;
	}
	
	@Unique private BoatExt ext() {
		return ((BoatDuck) this).bwe$getExt();
	}
}
