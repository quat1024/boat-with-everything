package agency.highlysuspect.boatwitheverything;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;

public interface ContainerExt extends Container, MenuConstructor {
	NonNullList<ItemStack> getItemStacks();
	
	default CompoundTag writeSaveData() {
		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, getItemStacks());
		return tag;
	}
	
	default void readSaveData(CompoundTag tag) {
		clearContent();
		ContainerHelper.loadAllItems(tag, getItemStacks());
	}
	
	default void drop(Boat boat, BoatExt ext) {
		for(ItemStack stack : getItemStacks()) {
			boat.spawnAtLocation(stack);
		}
		clearContent();
	}
}
