package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpongeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpongeBlock.class)
public interface AccessorSpongeBlock {
	@Invoker("removeWaterBreadthFirstSearch") boolean boatWithEverything$removeWaterBreadthFirstSearch(Level level, BlockPos blockPos);
}
