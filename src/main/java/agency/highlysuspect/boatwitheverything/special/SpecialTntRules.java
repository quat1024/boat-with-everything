package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

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
		
		//TntBlock.explode(boat.level, boat.blockPosition());
		Vec3 pos = SpecialBoatRules.positionOfBlock(boat);
		PrimedTnt primedTnt = new PrimedTnt(boat.level, pos.x, pos.y, pos.z, null);
		boat.level.addFreshEntity(primedTnt);
		boat.level.playSound(null, primedTnt.getX(), primedTnt.getY(), primedTnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
		boat.level.gameEvent(boat, GameEvent.PRIME_FUSE, pos);
	}
}
