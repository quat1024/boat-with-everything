package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.HackyEntityUpdateIds;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.jetbrains.annotations.Nullable;

public class SpecialChestRules implements SpecialBoatRules {
	@Override
	public @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		return new ChestContainerExt(boat, ext);
	}
	
	public static class ChestContainerExt extends ContainerExt.SimpleContainerImpl implements ContainerExt {
		public ChestContainerExt(Boat boat, BoatExt ext) {
			super(boat, ext, 27);
		}
		
		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
			return ChestMenu.threeRows(i, inventory, this);
		}
		
		private int oldWatchers = 0;
		private int watchers = 0;
		
		@Override
		public void startOpen(Player player) {
			watchers++;
			updateLidAndSounds();
		}
		
		@Override
		public void stopOpen(Player player) {
			watchers--;
			updateLidAndSounds();
		}
		
		private void updateLidAndSounds() {
			boolean wasOpen = oldWatchers > 0;
			boolean shouldOpen = watchers > 0;
			if(wasOpen != shouldOpen) {
				boat.level.broadcastEntityEvent(boat, shouldOpen ? HackyEntityUpdateIds.OPEN_CHEST : HackyEntityUpdateIds.CLOSE_CHEST); //see MixinEntity_HackyBoatUpdates
				
				if(shouldOpen) boat.playSound(SoundEvents.CHEST_OPEN);
				else boat.playSound(SoundEvents.CHEST_CLOSE);
			}
			oldWatchers = watchers;
		}
	}
}
