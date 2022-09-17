package agency.highlysuspect.boatwitheverything.fabric.integration.kahur;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.WeirdBlockRegistryThing;
import agency.highlysuspect.boatwitheverything.client.SpecialBoatRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.kahur.content.block.ConfettiCannonBlockEntity;
import com.unascribed.kahur.init.KBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class KahurIntegrationClient {
	public static void addMoreRenderers(WeirdBlockRegistryThing<SpecialBoatRenderer> r) {
		r.putBlock(new SpecialConfettiCannonRenderer(), KBlocks.CONFETTI_CANNON);
	}
	
	public static class SpecialConfettiCannonRenderer implements SpecialBoatRenderer {
		private static final BlockEntity be = new ConfettiCannonBlockEntity(BlockPos.ZERO, KBlocks.CONFETTI_CANNON.defaultBlockState()); 
		
		@Override
		public void render(Boat boat, BoatExt ext, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state, ItemStack stack) {
			DEFAULT.render(boat, ext, yaw, partialTicks, pose, bufs, light, state, stack);
			
			if(state.is(KBlocks.CONFETTI_CANNON)) {
				//noinspection deprecation
				be.setBlockState(state);
				
				//TODO: ConfettiCannonBlockEntity#ticksSinceFired
				
				pose.pushPose();
				pose.translate(-0.5, 0, -0.5);
				Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(be, pose, bufs, light, OverlayTexture.NO_OVERLAY);
				pose.popPose();
			}
		}
	}
}
