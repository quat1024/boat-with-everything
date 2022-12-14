package agency.highlysuspect.boatwitheverything.mixin.client;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.client.BoatWithEverythingClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatRenderer.class)
public class MixinBoatRenderer {
	@Unique private boolean noSpamPls = false;
	
	@Inject(
		method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/model/BoatModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
			shift = At.Shift.AFTER
		)
	)
	private void afterRenderingBoatModel(Boat boat, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, CallbackInfo ci) {
		BoatExt ext = ((BoatDuck) boat).bwe$getExt();
		BlockState state = ext.getBlockState();
		if(state == null) return; //just from this handler
		
		pose.pushPose();
		
		pose.scale(-1f, -1f, 1f); //undo weird boatstuff
		pose.translate(15/32d, -3/16d + 0.01, 0); //Move to a tiny bit above the back of the boat
		pose.mulPose(Vector3f.YP.rotationDegrees(-90)); //Idk
		pose.scale(0.8f, 0.8f, 0.8f); //Scale down so the block fits inside the boat
		
		try {
			BoatWithEverythingClient.INSTANCE.rendererRegistry.get()
				.get(state)
				.render(boat, ext, yaw, partialTicks, pose, bufs, light, state, ext.getItemStack());
		} catch (Exception e) {
			if(!noSpamPls) e.printStackTrace();
			noSpamPls = true;
		} finally {
			pose.popPose();
		}
	}
}
