package agency.highlysuspect.boatwitheverything.block;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.special.BoatRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class BoatLightBlockEntity extends BlockEntity {
	public BoatLightBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BoatWithEverything.INSTANCE.boatLightBlockEntityType, blockPos, blockState);
	}
	
	//kept here so there isn't a duplicate copy in the code responsible for placing light blocks
	public static BlockPos lightBlockPos(Boat boat) {
		Vec3 real = BoatRules.positionOfBlock(boat);
		return new BlockPos(real.x, real.y + 0.5d, real.z);
	}
	
	public static List<Boat> boatsAround(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(Boat.class, new AABB(pos).inflate(1));
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, BoatLightBlockEntity real) {
		if(!state.is(BoatWithEverything.INSTANCE.boatLightBlock)) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		
		//Check that there is still a light-emitting boat here, justifying the existence of this block
		int lightLevel = state.getValue(BoatLightBlock.LEVEL);
		for(Boat boat : boatsAround(level, pos)) {
			BoatExt ext = ((BoatDuck) boat).bwe$getExt();
			BoatRules rules = ext.getRules();
			if(rules == null) continue;
			
			if(Objects.equals(pos, lightBlockPos(boat)) && lightLevel == rules.light(boat, ext)) return; //happy return
		}
		
		//Couldn't find any
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}
}
