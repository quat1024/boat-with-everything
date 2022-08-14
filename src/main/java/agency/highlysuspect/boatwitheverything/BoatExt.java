package agency.highlysuspect.boatwitheverything;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defined like this (instead of a duck interface) because methods added via duck interface should be globally unique.
 * These are very commonly used methods and I want them to be nice to call.
 */
public interface BoatExt {
	void setBlockState(@Nullable BlockState state);
	BlockState getBlockState();
	
	void setItemStack(@NotNull ItemStack stack);
	@NotNull ItemStack getItemStack();
	
	default boolean hasBlockState() {
		return getBlockState() != null;
	}
	
	default boolean hasItemStack() {
		return !getItemStack().isEmpty();
	}
}
