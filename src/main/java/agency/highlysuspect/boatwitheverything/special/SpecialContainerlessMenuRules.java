package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("ClassCanBeRecord")
public class SpecialContainerlessMenuRules implements SpecialBoatRules {
	public SpecialContainerlessMenuRules(MenuConstructor cons) {
		this.cons = cons;
	}
	
	private final MenuConstructor cons;
	
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
				return cons.make(i, inventory, new WeirdBoatContainerLevelAccess(boat));
			}
		};
	}
	
	public interface MenuConstructor {
		AbstractContainerMenu make(int sequence, Inventory playerInventory, ContainerLevelAccess cla);
	}
	
	@SuppressWarnings("ClassCanBeRecord")
	public static class WeirdBoatContainerLevelAccess implements ContainerLevelAccess {
		public WeirdBoatContainerLevelAccess(Boat boat) {
			this.boat = boat;
		}
		
		public final Boat boat;
		
		@Override
		public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> biFunction) {
			return Optional.of(biFunction.apply(boat.level, boat.blockPosition()));
		}
		
		//see MixinItemCombinerMenu
		public boolean stillValid(Player player, Predicate<Block> blockPred) {
			BlockState stateInBoat = ((BoatDuck) boat).bwe$getExt().getBlockState();
			if(stateInBoat == null) return false;
			
			Block blockInBoat = stateInBoat.getBlock();
			if(!blockPred.test(blockInBoat)) return false;
			
			return player.distanceToSqr(boat.getX(), boat.getY(), boat.getZ()) <= 64.0;
		}
	}
}
