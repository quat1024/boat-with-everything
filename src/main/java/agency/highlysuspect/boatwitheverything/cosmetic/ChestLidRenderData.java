package agency.highlysuspect.boatwitheverything.cosmetic;

import agency.highlysuspect.boatwitheverything.BoatExt;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.entity.ChestLidController;

//Accessed on client *and* server btw
public class ChestLidRenderData implements RenderData {
	public final ChestLidController lidController = new ChestLidController();
	
	@Override
	public void tick(Boat boat, BoatExt ext) {
		lidController.tickLid();
	}
	
	public void setShouldBeOpen(boolean shouldBeOpen) {
		lidController.shouldBeOpen(shouldBeOpen);
	}
	
	public float getOpenness(float partialTicks) {
		return lidController.getOpenness(partialTicks);
	}
}
