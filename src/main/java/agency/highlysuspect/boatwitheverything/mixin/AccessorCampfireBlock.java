package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CampfireBlock.class)
public interface AccessorCampfireBlock {
	@Accessor("spawnParticles") boolean bwe$spawnLavaParticles();
	@Invoker("isSmokeSource") boolean bwe$isSmokeSource(BlockState state);
}
