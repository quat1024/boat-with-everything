package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HopperBlockEntity.class)
public interface AccessorHopperBlockEntity {
	@Invoker("getContainerAt") static Container getContainerAt(Level level, double x, double y, double z) {
		throw new IllegalStateException("mixin oof");
	}
}
