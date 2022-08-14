package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.cosmetic.ContainerExtWithLid;
import agency.highlysuspect.boatwitheverything.special.SpecialChestRules;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions") //casting shenanigans
@Mixin(Entity.class)
public class MixinEntity {
	@Inject(method = "onSyncedDataUpdated", at = @At("HEAD"))
	public void whenUpdatingSyncedData(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
		if(((Entity) (Object) this) instanceof Boat) {
			((BoatDuck) this).bwe$getExt().onSyncedDataUpdated(entityDataAccessor);
		}
	}
	
	@Inject(method = "handleEntityEvent", at = @At("HEAD"))
	public void whenHandlingEntityEvent(byte event, CallbackInfo ci) {
		//Yeah this is............. not great code! But it means I don't have to make my own packet lol
		
		if(((Entity) (Object) this) instanceof Boat) {
			BoatExt ext = ((BoatDuck) this).bwe$getExt();
			if(ext.getContainer() instanceof ContainerExtWithLid lid) {
				lid.setShouldBeOpen(event == 69);
			}
		}
	}
}
