package agency.highlysuspect.boatwitheverything;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;

public class BoatRenderHelper {
	public static void doIt(Boat boat, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state) {
		//Everything is fine
		
		VertexConsumer buf = bufs.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
		BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
		
		pose.pushPose();
		pose.scale(-1f, -1f, 1f); //lol idk
		pose.translate(-1/32d, -3/16d + 0.01, -0.5); //dont ask
		
		//scale around the middle
		pose.translate(0.5, 0, 0.5);
		pose.scale(0.8f, 0.8f, 0.8f);
		pose.translate(-0.5, 0, -0.5);
		
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(pose.last(), buf, state, model, 1, 1, 1, light, OverlayTexture.NO_OVERLAY);
		
		pose.popPose();
	}
}
