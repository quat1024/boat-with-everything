package agency.highlysuspect.boatwitheverything;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;

public interface ContainerExt extends Container, MenuConstructor {
	default boolean hasServerControlledInventory() {
		return true;
	}
	
	default CompoundTag writeSaveData() {
		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, getItemStacks());
		return tag;
	}
	
	default void readSaveData(CompoundTag tag) {
		clearContent();
		ContainerHelper.loadAllItems(tag, getItemStacks());
	}
	
	NonNullList<ItemStack> getItemStacks();
}
