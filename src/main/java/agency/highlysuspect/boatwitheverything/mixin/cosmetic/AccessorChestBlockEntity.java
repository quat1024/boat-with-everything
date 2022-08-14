package agency.highlysuspect.boatwitheverything.mixin.cosmetic;

import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChestBlockEntity.class)
public interface AccessorChestBlockEntity {
	@Accessor("chestLidController") ChestLidController bwe$getChestLidController();
}
