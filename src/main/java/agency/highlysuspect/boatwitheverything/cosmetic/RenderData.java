package agency.highlysuspect.boatwitheverything.cosmetic;

import agency.highlysuspect.boatwitheverything.BoatExt;
import net.minecraft.world.entity.vehicle.Boat;

/**
 * Per-boat storage that persists between frames.
 */
public interface RenderData {
	default void tick(Boat boat, BoatExt ext) {
		//Called every frame on the client side
	}
}
