package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.mixin.AccessorAbstractProjectileDispenseBehavior;
import agency.highlysuspect.boatwitheverything.mixin.AccessorDispenserBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SpecialDispenserRules extends SpecialDropperRules {
	@Override
	public @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		return new DispenserContainerExt(boat, ext);
	}
	
	public static class DispenserContainerExt extends SpecialDropperRules.DropperContainerExt {
		public DispenserContainerExt(Boat boat, BoatExt ext) {
			super(boat, ext);
		}
		
		@Override
		public Behavior getBehavior(ItemStack stack) {
			DispenseItemBehavior vanillaDispenseBehavior = AccessorDispenserBlock.bwe$getDispenserRegistry().get(stack.getItem());
			if(vanillaDispenseBehavior instanceof AbstractProjectileDispenseBehavior proj) {
				return new ProjectileWeirdBehavior(proj);
			}
			
			//TODO: More dispener behaviors !
			return null;
		}
	}
	
	public static class ProjectileWeirdBehavior implements SpecialDropperRules.DropperContainerExt.Behavior {
		public ProjectileWeirdBehavior(AbstractProjectileDispenseBehavior vanilla) {
			this.access = (AccessorAbstractProjectileDispenseBehavior) vanilla;
		}
		
		public final AccessorAbstractProjectileDispenseBehavior access;
		
		@Override
		public ItemStack dispense(Boat boat, BoatExt ext, ContainerExt cont, Direction facing, ItemStack stackinSlot, Vec3 dropperPos, Vec3 dropperNormal, Vec3 dropperTangent, Vec3 dropperBitangent) {
			Vec3 pos = dropperPos.add(dropperNormal.scale(0.6));
			
			Projectile p = access.bwe$getProjectile(boat.level, pos, stackinSlot);
			
			p.shoot(dropperNormal.x, dropperNormal.y, dropperNormal.z, access.bwe$getUncertainty(), access.bwe$getPower());
			boat.level.addFreshEntity(p);
			
			DropperContainerExt.Behavior.dispenseSound(boat, dropperPos);
			
			ItemStack leftover = stackinSlot.copy();
			leftover.shrink(1);
			return leftover;
		}
	}
}
