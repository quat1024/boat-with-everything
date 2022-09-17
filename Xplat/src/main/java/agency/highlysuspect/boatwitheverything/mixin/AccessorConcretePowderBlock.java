package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConcretePowderBlock.class)
public interface AccessorConcretePowderBlock {
	@Accessor("concrete") BlockState bwe$thatsConkCreteBabey();
}
