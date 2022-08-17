package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.mixin.AccessorSimpleContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;

public interface ContainerExt extends Container, MenuConstructor {
	NonNullList<ItemStack> getItemStacks(); //Needed to impl ContainerEntity on Boat
	
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
	
	abstract class SimpleContainerImpl extends SimpleContainer implements ContainerExt {
		public SimpleContainerImpl(Boat boat, BoatExt ext, int i) {
			super(i);
			this.boat = boat;
			this.ext = ext;
		}
		
		public SimpleContainerImpl(Boat boat, BoatExt ext, ItemStack... itemStacks) {
			super(itemStacks);
			this.boat = boat;
			this.ext = ext;
		}
		
		public final Boat boat;
		public final BoatExt ext;
		
		@Override
		public NonNullList<ItemStack> getItemStacks() {
			return ((AccessorSimpleContainer) this).bwe$items();
		}
	}
}
