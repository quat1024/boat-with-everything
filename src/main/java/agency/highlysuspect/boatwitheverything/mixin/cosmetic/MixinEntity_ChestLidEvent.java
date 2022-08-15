package agency.highlysuspect.boatwitheverything.mixin.cosmetic;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.cosmetic.ChestLidRenderData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(Entity.class)
public class MixinEntity_ChestLidEvent {
	@Inject(method = "handleEntityEvent", at = @At("HEAD"))
	public void whenHandlingEntityEvent(byte event, CallbackInfo ci) {
		//Yeah this is............. not great code! But it means I don't have to make my own packet lol
		
		if(((Entity) (Object) this) instanceof Boat) {
			BoatExt ext = ((BoatDuck) this).bwe$getExt();
			if(ext.getRenderAttachmentData() instanceof ChestLidRenderData lid) {
				lid.setShouldBeOpen(event == 69);
			}
		}
	}
}
