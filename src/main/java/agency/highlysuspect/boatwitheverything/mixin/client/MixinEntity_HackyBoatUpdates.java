package agency.highlysuspect.boatwitheverything.mixin.client;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.HackyEntityUpdateIds;
import agency.highlysuspect.boatwitheverything.special.SpecialChestRules;
import agency.highlysuspect.boatwitheverything.special.SpecialNoteBlockRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(Entity.class)
public class MixinEntity_HackyBoatUpdates {
	@Inject(method = "handleEntityEvent", at = @At("HEAD"))
	public void whenHandlingEntityEvent(byte event, CallbackInfo ci) {
		//Yeah this is............. not great code! But it means I don't have to make my own packet lol
		
		if(((Entity) (Object) this) instanceof Boat boat) {
			BoatExt ext = ((BoatDuck) this).bwe$getExt();
			
			if(event == HackyEntityUpdateIds.FILL_BOAT_WITH_WATER_LOL) {
				ext.setForceSink();
				return;
			}
			
			if(ext.getRenderAttachmentData() instanceof SpecialChestRules.ChestLidRenderData lid) {
				lid.setShouldBeOpen(event == HackyEntityUpdateIds.OPEN_CHEST);
				return;
			}
			
			if(ext.getRules() instanceof SpecialNoteBlockRules dootDoot) {
				dootDoot.dootClient(boat, ext);
			}
		}
	}
}
