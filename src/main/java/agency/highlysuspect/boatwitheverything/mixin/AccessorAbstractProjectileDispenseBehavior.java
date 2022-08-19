package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractProjectileDispenseBehavior.class)
public interface AccessorAbstractProjectileDispenseBehavior {
	@Invoker("getProjectile") Projectile bwe$getProjectile(Level level, Position position, ItemStack stack);
	@Invoker("getUncertainty") float bwe$getUncertainty();
	@Invoker("getPower") float bwe$getPower();
}
