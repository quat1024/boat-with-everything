package agency.highlysuspect.boatwitheverything.mixin.backport1_18;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.special.BoatRules;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Boat.class)
public class MixinBoat_BackportStuff {
	@ModifyConstant(method = "positionRider", constant = @Constant(floatValue = 0f, ordinal = 0))
	float boatWithEverything$positionRiderModifyConstant(float original) {
		BoatRules rules = ((BoatDuck) this).bwe$getExt().getRules();
		if(rules == null || !rules.consumesPassengerSlot()) return original;
		else return original + 0.15f;
	}
}
