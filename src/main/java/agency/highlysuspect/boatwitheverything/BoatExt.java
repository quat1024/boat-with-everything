package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.cosmetic.RenderData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Boat extensions.
 * 
 * Defined like this (instead of a duck interface) because methods added via duck interface should be globally unique.
 * These are very commonly used methods and I want them to be nice to call.
 */
public interface BoatExt {
	@Nullable BlockState getBlockState();
	void setBlockState(@Nullable BlockState state);
	
	@NotNull ItemStack getItemStack();
	void setItemStack(@NotNull ItemStack stack);
	
	@Nullable ContainerExt getContainer();
	
	default boolean hasBlockState() {
		return getBlockState() != null;
	}
	
	default boolean hasItemStack() {
		return !getItemStack().isEmpty();
	}
	
	default void clearBlockState() {
		setBlockState(null);
	}
	
	default void clearItemStack() {
		setItemStack(ItemStack.EMPTY);
	}
	
	@Nullable SpecialBoatRules getRules();
	int getMaxPassengers();
	void onSyncedDataUpdated(EntityDataAccessor<?> accessor);
	
	RenderData getRenderAttachmentData();
	void setRenderAttachmentData(RenderData whatever);
}
