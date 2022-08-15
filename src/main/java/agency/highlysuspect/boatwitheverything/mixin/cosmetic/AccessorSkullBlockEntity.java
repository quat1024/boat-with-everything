package agency.highlysuspect.boatwitheverything.mixin.cosmetic;

import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SkullBlockEntity.class)
public interface AccessorSkullBlockEntity {
	@Accessor("mouthTickCount") void bwe$setMouthTickCount(int newTickCount);
	@Accessor("isMovingMouth") void bwe$setIsMovingMouth(boolean isMovingMouth);
}
