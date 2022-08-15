package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("ClassCanBeRecord")
public class WeirdBoatContainerLevelAccess implements ContainerLevelAccess {
	public WeirdBoatContainerLevelAccess(Boat boat) {
		this.boat = boat;
	}
	
	public final Boat boat;
	
	@Override
	public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> biFunction) {
		return Optional.of(biFunction.apply(boat.level, boat.blockPosition()));
	}
	
	public boolean stillValid(Player player, Predicate<Block> blockPred) {
		BlockState stateInBoat = ((BoatDuck) boat).bwe$getExt().getBlockState();
		if(stateInBoat == null) return false;
		
		Block blockInBoat = stateInBoat.getBlock();
		if(!blockPred.test(blockInBoat)) return false;
		
		return player.distanceToSqr(boat.getX(), boat.getY(), boat.getZ()) <= 64.0;
	}
}
