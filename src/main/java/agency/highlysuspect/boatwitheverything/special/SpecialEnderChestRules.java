package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.DelegatingContainer;
import agency.highlysuspect.boatwitheverything.HackyEntityUpdateIds;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.mixin.AccessorSimpleContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SpecialEnderChestRules implements SpecialBoatRules {
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
				return new EnderChestContainerExt(player.getEnderChestInventory(), boat, ext).createMenu(i, inventory, player);
			}
		};
	}
	
	public static class EnderChestContainerExt extends DelegatingContainer<PlayerEnderChestContainer> implements ContainerExt {
		public EnderChestContainerExt(PlayerEnderChestContainer delegate, Boat boat, BoatExt ext) {
			super(delegate);
			this.boat = boat;
			this.ext = ext;
		}
		
		private final Boat boat;
		private final BoatExt ext;
		
		private int watchers = 0, oldWatchers = 0;
		
		@Override
		public NonNullList<ItemStack> getItemStacks() {
			return ((AccessorSimpleContainer) delegate).bwe$items();
		}
		
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
				
				if(shouldOpen) boat.playSound(SoundEvents.ENDER_CHEST_OPEN);
				else boat.playSound(SoundEvents.ENDER_CHEST_CLOSE);
			}
			oldWatchers = watchers;
		}
		
		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
			return ChestMenu.threeRows(i, inventory, this);
		}
	}
}
