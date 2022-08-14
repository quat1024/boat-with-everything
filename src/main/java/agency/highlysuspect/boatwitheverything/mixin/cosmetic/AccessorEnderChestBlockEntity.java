package agency.highlysuspect.boatwitheverything.mixin.cosmetic;

import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderChestBlockEntity.class)
public interface AccessorEnderChestBlockEntity {
	@Accessor("chestLidController") ChestLidController bwe$getChestLidController();
}
