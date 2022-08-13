package agency.highlysuspect.boatwitheverything;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BoatRenderHelper {
	public static void doIt(Boat boat, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state) {
		//Everything is fine
		pose.pushPose();
		pose.scale(-1f, -1f, 1f); //lol idk
		pose.translate(-1/32d, -3/16d + 0.01, -0.5); //MAGIC NUMBER DO NOT TOUCH
		
		//scale and rotate around the middle
		pose.translate(0.5, 0, 0.5);
		pose.mulPose(Vector3f.YP.rotationDegrees(-90));
		pose.scale(0.8f, 0.8f, 0.8f);
		//haxx for chests because BlockEntityWithoutItemRenderer won't bring over the rotation
		if(state.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			pose.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
		}
		pose.translate(-0.5, 0, -0.5);
		
		//weird utility method lets go !
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, bufs, light, OverlayTexture.NO_OVERLAY);
		
		pose.popPose();
	}
}
