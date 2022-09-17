package agency.highlysuspect.boatwitheverything.block;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoatLightBlock extends LightBlock implements EntityBlock {
	public BoatLightBlock() {
		super(BlockBehaviour.Properties.copy(Blocks.LIGHT));
	}
	
	public BlockState withLevel(int level) {
		return defaultBlockState().setValue(LEVEL, level);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BoatLightBlockEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> realType) {
		return BoatWithEverything.blockTickersCanKissMyAss(realType, BoatWithEverything.INSTANCE.boatLightBlockEntityType, BoatLightBlockEntity::tick);
	}
}
