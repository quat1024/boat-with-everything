package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.mixin.AccessorSimpleContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SpecialBarrelRules implements SpecialBoatRules {
	@Override
	public @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		System.out.println("SpecialBarrelRules#makeNewContainer @ 19");
		return new BarrelContainerExt();
	}
	
	public static class BarrelContainerExt extends SimpleContainer implements ContainerExt {
		public BarrelContainerExt() {
			super(27);
		}
		
		@Override
		public NonNullList<ItemStack> getItemStacks() {
			return ((AccessorSimpleContainer) this).bwe$items();
		}
		
		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
			return null;
		}
	}
}
