package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.cosmetic.ChestLidControllerDuck;
import agency.highlysuspect.boatwitheverything.SpecialBoatRenderer;
import agency.highlysuspect.boatwitheverything.cosmetic.ContainerExtWithLid;
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
		if(ext.getContainer() instanceof ContainerExtWithLid ccext) {
			((ChestLidControllerDuck) lidController).bwe$setOpenness(ccext.getOpenNess(partialTicks));
		} else {
			((ChestLidControllerDuck) lidController).bwe$setOpenness(0);
		}
		
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(be, pose, bufs, light, OverlayTexture.NO_OVERLAY);
	}
}
