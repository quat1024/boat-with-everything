package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.mixin.AccessorBlockRenderDispatcher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public interface SpecialBoatRenderer {
	void render(Boat boat, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state, ItemStack stack);
	
	//renderSingleBlock is an oddball method for some rendering edge-cases in the game, like drawing the block an enderman is holding or the TNT inside a tnt minecart.
	//It generally works okay but there are some cases it does not render very well.
	SpecialBoatRenderer DEFAULT = (boat, yaw, pt, pose, bufs, light, state, stack) -> {
		pose.translate(-0.5, 0, -0.5);
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, bufs, light, OverlayTexture.NO_OVERLAY);
		pose.translate(0.5, 0, 0.5);
	};
	
	//Some blocks have a RenderShape of INVISIBLE which makes renderSingleBlock skip them. I'm not sure what this RenderShape is supposed to do, because it's usually
	//not placed on blocks that are actually invisible (e.g. banners, skulls). Here we invoke BlockEntityWithoutLevelRenderer directly instead of asking renderSingleBlock
	//to do it.
	SpecialBoatRenderer USING_BEWLR = (boat, yaw, pt, pose, bufs, light, state_, stack) -> {
		pose.translate(-0.5, 0, -0.5);
		((AccessorBlockRenderDispatcher) Minecraft.getInstance().getBlockRenderer())
			.boatWithEverything$getBlockEntityWithoutLevelRenderer()
			.renderByItem(stack, ItemTransforms.TransformType.NONE, pose, bufs, light, OverlayTexture.NO_OVERLAY);
		pose.translate(0.5, 0, 0.5);
	};
		
	
	static SpecialBoatRenderer get(BlockState state) {
		//Beds, always rotated to face inside the boat. The BlockEntityRenderer takes care of drawing both halves of the bed
		if(state.getBlock() instanceof BedBlock) {
			return (boat, yaw, pt, pose, bufs, light, state_, stack) -> {
				pose.mulPose(Vector3f.YP.rotationDegrees(180));
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_, stack);
			};
		}
		
		//Most double-height blocks, like doors and big flowers
		if(state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
			return (boat, yaw, pt, pose, bufs, light, state_, stack) -> {
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_, stack);
				pose.translate(0, 1, 0);
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), stack);
			};
		}
		
		//Haxx for certain block entities like enderchests; renderSingleBlock doesn't apply the rotation from the blockstate
		if(state.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return (boat, yaw, pt, pose, bufs, light, state_, stack) -> {
				pose.mulPose(Vector3f.YP.rotationDegrees(-state_.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
				DEFAULT.render(boat, yaw, pt, pose, bufs, light, state_, stack);
			};
		}
		
		//Banners - renderSingleBlock skips them because of the RenderShape, and I want to position them differently
		if(state.getBlock() instanceof BannerBlock) {
			return (boat, yaw, pt, pose, bufs, light, state_, stack) -> {
				pose.mulPose(Vector3f.YP.rotationDegrees(180));
				pose.translate(0, 7/16d, 0.59); //Sticks the banner's post into the side of the boat
				USING_BEWLR.render(boat, yaw, pt, pose, bufs, light, state_, stack);
			};
		}
		
		//Skulls, also skipped in renderSingleBlock. And I need to apply the funny rotation property
		if(state.getBlock() instanceof SkullBlock && state.hasProperty(BlockStateProperties.ROTATION_16)) {
			return (boat, yaw, pt, pose, bufs, light, state_, stack) -> {
				pose.mulPose(Vector3f.YP.rotationDegrees(180 + -22.5f * state_.getValue(BlockStateProperties.ROTATION_16)));
				USING_BEWLR.render(boat, yaw, pt, pose, bufs, light, state_, stack); //Will probably need adjustment to do the enderdragon skull animation
			};
		}
		
		//Everything else
		else return DEFAULT;
	}
}