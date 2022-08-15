package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRenderer;
import agency.highlysuspect.boatwitheverything.mixin.cosmetic.AccessorBannerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SpecialBannerRenderer implements SpecialBoatRenderer {
	private final BannerBlockEntity bannerBe = new BannerBlockEntity(BlockPos.ZERO, Blocks.WHITE_BANNER.defaultBlockState());
	
	@Override
	public void render(Boat boat, BoatExt ext, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state, ItemStack stack) {
		if(!(state.getBlock() instanceof BannerBlock bannerBlock)) return;
		
		bannerBe.fromItem(stack, bannerBlock.getColor());
		List<Pair<Holder<BannerPattern>, DyeColor>> patterns = bannerBe.getPatterns();
		
		pose.mulPose(Vector3f.YP.rotationDegrees(180));
		pose.translate(0, 0.9375, 0.59); //Sticks the banner's post into the side of the boat
		
		BlockEntityRenderer<BannerBlockEntity> vRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(bannerBe);
		if(!(vRenderer instanceof BannerRenderer vanillaBannerRenderer)) return;
		
		ModelPart flag = ((AccessorBannerRenderer) vanillaBannerRenderer).bwe$flag();
		ModelPart pole = ((AccessorBannerRenderer) vanillaBannerRenderer).bwe$pole();
		ModelPart bar = ((AccessorBannerRenderer) vanillaBannerRenderer).bwe$bar();
		
		//Copied out of the vanilla BannerRenderer mostly
		pole.visible = true; //will get reset by the vanilla renderer dont worry
		pose.scale(0.666f, -0.666f, -0.666f);
		VertexConsumer vc = ModelBakery.BANNER_BASE.buffer(bufs, RenderType::entitySolid);
		pole.render(pose, vc, light, OverlayTexture.NO_OVERLAY);
		bar.render(pose, vc, light, OverlayTexture.NO_OVERLAY);
		
		flag.xRot = (float) (boat.getDeltaMovement().length() * -1.2);
		flag.y = -32f;
		
		BannerRenderer.renderPatterns(pose, bufs, light, OverlayTexture.NO_OVERLAY, flag, ModelBakery.BANNER_BASE, true, patterns);
	}
}
