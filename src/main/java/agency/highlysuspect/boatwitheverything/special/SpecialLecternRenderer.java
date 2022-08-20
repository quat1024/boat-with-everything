package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpecialLecternRenderer implements SpecialBoatRenderer {
	LecternBlockEntity lecternBe = new LecternBlockEntity(BlockPos.ZERO, Blocks.LECTERN.defaultBlockState());
	
	@Override
	public void render(Boat boat, BoatExt ext, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state, ItemStack stack) {
		DEFAULT.render(boat, ext, yaw, partialTicks, pose, bufs, light, state, stack);
		
		pose.translate(-0.5, 0, -0.5);
		
		//noinspection deprecation
		lecternBe.setBlockState(state);
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(lecternBe, pose, bufs, light, OverlayTexture.NO_OVERLAY);
	}
}
