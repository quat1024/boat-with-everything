package agency.highlysuspect.boatwitheverything;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public interface SpecialBoatRenderer {
	void render(Boat boat, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state);
	
	SpecialBoatRenderer DEFAULT = (boat, yaw, pt, pose, bufs, light, state) -> Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, bufs, light, OverlayTexture.NO_OVERLAY);
	
	static SpecialBoatRenderer get(BlockState state) {
		//Case for beds
		if(state.getBlock() instanceof BedBlock) {
			return (boat, yaw, pt, pose, bufs, light, state_) -> {
				pose.translate(0.5, 0, 0.5);
				pose.mulPose(Vector3f.YP.rotationDegrees(180));
				pose.translate(-0.5, 0, -0.5);
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_);
			};
		}
		
		//Case that covers most double-blocks, like doors and big flowers
		if(state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
			return (boat, yaw, pt, pose, bufs, light, state_) -> {
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_);
				pose.translate(0, 1, 0);
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
			};
		}
		
		//Haxx for certain block entities like enderchests b/c BlockEntityWithoutLevelRenderer doesn't bring over the rotation
		if(state.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return (boat, yaw, pt, pose, bufs, light, state_) -> {
				pose.translate(0.5, 0, 0.5);
				pose.mulPose(Vector3f.YP.rotationDegrees(-state_.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
				pose.translate(-0.5, 0, -0.5);
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_);
			};
		}
		
		//Everything else
		else return DEFAULT;
	}
}
