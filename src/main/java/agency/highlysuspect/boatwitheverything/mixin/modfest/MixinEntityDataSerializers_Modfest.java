package agency.highlysuspect.boatwitheverything.mixin.modfest;

import agency.highlysuspect.boatwitheverything.ModfestHackery;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.spongepowered.asm.mixin.Mixin;

/**
 * I am SOOOO SORRY, i know how fragile these are, but there isn't one for vec3s that i know of
 * There is one for "Rotations" but it's a float vector
 */
@Mixin(value = EntityDataSerializers.class, priority = 1234)
public class MixinEntityDataSerializers_Modfest {
	static {
		EntityDataSerializers.registerSerializer(ModfestHackery.OPTIONAL_VEC3_SERIALIZER);
	}
}
