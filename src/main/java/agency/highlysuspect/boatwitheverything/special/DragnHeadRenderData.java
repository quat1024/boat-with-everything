package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.RenderData;
import net.minecraft.world.entity.vehicle.Boat;

/**
 * Not a typo :dragnthink:
 */
public class DragnHeadRenderData implements RenderData {
	public int ticks;
	public boolean powered;
	
	@Override
	public void tick(Boat boat, BoatExt ext) {
		powered = SpecialBoatRules.isPowered(boat);
		if(powered) ticks++;
	}
}
