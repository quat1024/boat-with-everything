package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.TntBlock;

public class SpecialTntRules implements SpecialBoatRules {
	@Override
	public void tick(Boat boat, BoatExt ext) {
		if(SpecialBoatRules.isPowered(boat)) kaboom(boat, ext);
	}
	
	@Override
	public boolean hurt(Boat boat, BoatExt ext, DamageSource source) {
		if(source.getDirectEntity() instanceof Projectile proj && proj.isOnFire() && proj.mayInteract(boat.level, boat.blockPosition())) {
			if(!boat.level.isClientSide) kaboom(boat, ext);
			return true;
		}
		return false;
	}
	
	private void kaboom(Boat boat, BoatExt ext) {
		ext.clearBlockState();
		ext.clearItemStack();
		TntBlock.explode(boat.level, boat.blockPosition());
	}
}
