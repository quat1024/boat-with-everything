package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FlowerPotBlock.class)
public interface AccessorFlowerPotBlock {
	@Accessor("POTTED_BY_CONTENT") Map<Block, Block> bwe$getPottedByContent();
	@Accessor("content") Block bwe$content();
}
