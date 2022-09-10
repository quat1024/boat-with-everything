package agency.highlysuspect.boatwitheverything;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

//The opposite of a good time porting.
public class Starboarding {
	public static void playSound(Entity ent, SoundEvent evt) {
		ent.playSound(evt, 1f, 1f);
	}
}
