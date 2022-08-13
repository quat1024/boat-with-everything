package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockRenderDispatcher.class)
public interface AccessorBlockRenderDispatcher {
	@Accessor("blockEntityRenderer") BlockEntityWithoutLevelRenderer boatWithEverything$getBlockEntityWithoutLevelRenderer();
}
