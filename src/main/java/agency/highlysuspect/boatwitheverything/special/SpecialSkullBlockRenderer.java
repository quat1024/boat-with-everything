package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRenderer;
import agency.highlysuspect.boatwitheverything.cosmetic.DragnHeadRenderData;
import agency.highlysuspect.boatwitheverything.mixin.cosmetic.AccessorSkullBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SpecialSkullBlockRenderer implements SpecialBoatRenderer {
	private static final SkullBlockEntity dragnBe = new SkullBlockEntity(BlockPos.ZERO, Blocks.DRAGON_HEAD.defaultBlockState());
	
	@Override
	public void render(Boat boat, BoatExt ext, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state, ItemStack stack) {
		if(state.getBlock() == Blocks.DRAGON_HEAD) {
			if(!(ext.getRenderAttachmentData() instanceof DragnHeadRenderData)) ext.setRenderAttachmentData(new DragnHeadRenderData());
			DragnHeadRenderData dhrd = (DragnHeadRenderData) ext.getRenderAttachmentData(); 
			
			((AccessorSkullBlockEntity) dragnBe).bwe$setIsMovingMouth(dhrd.powered);
			((AccessorSkullBlockEntity) dragnBe).bwe$setMouthTickCount(dhrd.ticks);
			
			//Doesn't pass partialTicks through for some reason???
			//Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(dragnBe, pose, bufs, light, OverlayTexture.NO_OVERLAY);
			BlockEntityRenderer<SkullBlockEntity> rend = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(dragnBe);
			assert rend != null;
			
			pose.translate(-0.5, 0, -1.445); //This one needs specifically a 0.5 shift for some reason :whyfest: also mount it on the back of the boat ig
			rend.render(dragnBe, partialTicks, pose, bufs, light, OverlayTexture.NO_OVERLAY);
		} else {
			if(state.hasProperty(BlockStateProperties.ROTATION_16))	pose.mulPose(Vector3f.YP.rotationDegrees(180 + -22.5f * state.getValue(BlockStateProperties.ROTATION_16)));
			USING_BEWLR.render(boat, ext, yaw, partialTicks, pose, bufs, light, state, stack); //Will probably need adjustment to do the enderdragon skull animation
		}
	}
}
