package agency.highlysuspect.boatwitheverything;

import net.minecraft.world.phys.Vec3;

public interface EntityDuck {
	/**
	 * Very dangerous method. Sets the player position without updating the player's
	 * hitbox or what chunk they're in or anything else. It must be set back to the original value ASAP.
	 */
	void setPositionSuperRawSuperDangerous(Vec3 pos);
}
