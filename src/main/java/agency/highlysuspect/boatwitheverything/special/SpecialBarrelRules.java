package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class SpecialBarrelRules implements SpecialBoatRules {
	@Override
	public @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		return new BarrelContainerExt(boat, ext);
	}
	
	public static class BarrelContainerExt extends ContainerExt.SimpleContainerImpl implements ContainerExt {
		public BarrelContainerExt(Boat boat, BoatExt ext) {
			super(boat, ext, 27);
		}
		
		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
			return ChestMenu.threeRows(i, inventory, this);
		}
		
		private int watchers = 0;
		
		@Override
		public void startOpen(Player player) {
			watchers++;
			updateStateAndSounds();
		}
		
		@Override
		public void stopOpen(Player player) {
			watchers--;
			updateStateAndSounds();
		}
		
		private void updateStateAndSounds() {
			BlockState currentState = ext.getBlockState();
			if(currentState == null || !currentState.hasProperty(BlockStateProperties.OPEN)) return; //idk
			boolean wasOpen = currentState.getValue(BlockStateProperties.OPEN);
			boolean shouldOpen = watchers > 0;
			
			if(wasOpen != shouldOpen) {
				ext.setBlockState(currentState.setValue(BlockStateProperties.OPEN, shouldOpen));
				
				if(shouldOpen) boat.playSound(SoundEvents.BARREL_OPEN);
				else boat.playSound(SoundEvents.BARREL_CLOSE);
			}
		}
	}
}
