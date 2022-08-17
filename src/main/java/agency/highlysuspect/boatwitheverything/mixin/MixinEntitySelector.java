package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public class MixinEntitySelector {
	@Shadow @Final @Mutable public static Predicate<Entity> CONTAINER_ENTITY_SELECTOR;
	
	static {
		CONTAINER_ENTITY_SELECTOR = CONTAINER_ENTITY_SELECTOR.and(ent -> !(BoatWithEverything.HOPPER_SKIP_THIS_BOAT_PLEASE.get() == ent));
	}
}
