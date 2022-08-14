package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.TntBlock;

public class SpecialTntRules implements SpecialBoatRules {
	@Override
	public void tick(Boat boat, BoatExt ext) {
		if(SpecialBoatRules.isPowered(boat)) {
			ext.clearBlockState();
			ext.clearItemStack();
			TntBlock.explode(boat.level, boat.blockPosition());
		}
	}
}
