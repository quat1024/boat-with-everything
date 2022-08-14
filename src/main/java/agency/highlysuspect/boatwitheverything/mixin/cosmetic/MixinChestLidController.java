package agency.highlysuspect.boatwitheverything.mixin.cosmetic;

import agency.highlysuspect.boatwitheverything.cosmetic.ChestLidControllerDuck;
import net.minecraft.world.level.block.entity.ChestLidController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChestLidController.class)
public class MixinChestLidController implements ChestLidControllerDuck {
	@Shadow private float openness;
	@Shadow private float oOpenness;
	
	public void bwe$setOpenness(float openness) {
		this.openness = oOpenness = openness;
	}
}
