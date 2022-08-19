package agency.highlysuspect.boatwitheverything.mixin;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DispenserBlock.class)
public interface AccessorDispenserBlock {
	@Accessor("DISPENSER_REGISTRY") static Map<Item, DispenseItemBehavior> bwe$getDispenserRegistry() {
		throw new IllegalStateException("mixin poot");
	}
}
