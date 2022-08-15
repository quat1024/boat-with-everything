package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LoomMenu;
import org.jetbrains.annotations.Nullable;

public class SpecialLoomRules implements SpecialBoatRules {
	@Override
	public @Nullable MenuProvider getMenuProvider(Boat boat, BoatExt ext, Player player) {
		return new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return boat.getDisplayName();
			}
			
			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
				return new LoomMenu(i, inventory, new WeirdBoatContainerLevelAccess(boat));
			}
		};
	}
}