package agency.highlysuspect.boatwitheverything;

import net.minecraft.world.entity.vehicle.Boat;

public interface BoatDuck {
	BoatExt bwe$getExt();
	
	static BoatDuck cast(Boat boat) {
		return (BoatDuck) boat;
	}
}
