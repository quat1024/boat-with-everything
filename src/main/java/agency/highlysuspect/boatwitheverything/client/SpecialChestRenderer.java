package agency.highlysuspect.boatwitheverything.client;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.special.SpecialChestRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Function;

public class SpecialChestRenderer<T extends BlockEntity> implements SpecialBoatRenderer {
	public SpecialChestRenderer(T be, Function<T, ChestLidController> lidControllerGetter) {
		this.be = be;
		this.getter = lidControllerGetter;
	}
	
	private final T be;
	private final Function<T, ChestLidController> getter;
	
	@Override
	public void render(Boat boat, BoatExt ext, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state, ItemStack stack) {
		pose.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
		pose.translate(-0.5, 0, -0.5);
		
		ChestLidController lidController = getter.apply(be);
		if(ext.getRenderAttachmentData() instanceof SpecialChestRules.ChestLidRenderData clrd) {
			((ChestLidControllerDuck) lidController).bwe$setOpenness_ClientSide(clrd.getOpenness(partialTicks));
		} else {
			ext.setRenderAttachmentData(new SpecialChestRules.ChestLidRenderData());
			((ChestLidControllerDuck) lidController).bwe$setOpenness_ClientSide(0);
		}
		
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(be, pose, bufs, light, OverlayTexture.NO_OVERLAY);
	}
}
