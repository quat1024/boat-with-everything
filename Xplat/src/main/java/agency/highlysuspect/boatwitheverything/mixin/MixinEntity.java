package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.EntityDuck;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions") //casting shenanigans
@Mixin(Entity.class)
public class MixinEntity implements EntityDuck {
	@Shadow private Vec3 position;
	
	@Inject(method = "onSyncedDataUpdated", at = @At("HEAD"))
	public void whenUpdatingSyncedData(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
		if(((Entity) (Object) this) instanceof Boat) {
			((BoatDuck) this).bwe$getExt().onSyncedDataUpdated(entityDataAccessor);
		}
	}
	
	@Override
	public void setPositionSuperRawSuperDangerous(Vec3 pos) {
		position = pos;
	}
}
