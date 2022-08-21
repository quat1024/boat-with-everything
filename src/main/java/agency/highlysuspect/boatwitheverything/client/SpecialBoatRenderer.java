package agency.highlysuspect.boatwitheverything.client;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.mixin.client.AccessorBlockRenderDispatcher;
import agency.highlysuspect.boatwitheverything.mixin.client.AccessorChestBlockEntity;
import agency.highlysuspect.boatwitheverything.mixin.client.AccessorEnderChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;

public interface SpecialBoatRenderer {
	void render(Boat boat, BoatExt ext, float yaw, float partialTicks, PoseStack pose, MultiBufferSource bufs, int light, BlockState state, ItemStack stack);
	
	//renderSingleBlock is an oddball method for some rendering edge-cases in the game, like drawing the block an enderman is holding or the TNT inside a tnt minecart.
	//It generally works okay but there are some cases it does not render very well.
	SpecialBoatRenderer DEFAULT = (boat, ext, yaw, pt, pose, bufs, light, state, stack) -> {
		pose.translate(-0.5, 0, -0.5);
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, bufs, light, OverlayTexture.NO_OVERLAY);
		pose.translate(0.5, 0, 0.5);
	};
	
	//Some blocks have a RenderShape of INVISIBLE which makes renderSingleBlock skip them. I'm not sure what this RenderShape is supposed to do, because it's usually
	//not placed on blocks that are actually invisible (e.g. banners, skulls). Here we invoke BlockEntityWithoutLevelRenderer directly instead of asking renderSingleBlock
	//to do it.
	SpecialBoatRenderer USING_BEWLR = (boat, ext, yaw, pt, pose, bufs, light, state_, stack) -> {
		pose.translate(-0.5, 0, -0.5);
		((AccessorBlockRenderDispatcher) Minecraft.getInstance().getBlockRenderer())
			.boatWithEverything$getBlockEntityWithoutLevelRenderer()
			.renderByItem(stack, ItemTransforms.TransformType.NONE, pose, bufs, light, OverlayTexture.NO_OVERLAY);
		pose.translate(0.5, 0, 0.5);
	};
	
	SpecialBoatRenderer CHEST = new SpecialChestRenderer<>(new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState()), chest -> ((AccessorChestBlockEntity) chest).bwe$getChestLidController_ClientSide());
	SpecialBoatRenderer ENDER_CHEST = new SpecialChestRenderer<>(new EnderChestBlockEntity(BlockPos.ZERO, Blocks.ENDER_CHEST.defaultBlockState()), chest -> ((AccessorEnderChestBlockEntity) chest).bwe$getChestLidController_ClientSide());
	SpecialBoatRenderer LECTERN = new SpecialLecternRenderer();
	
	SpecialBoatRenderer BANNER = new SpecialBannerRenderer();
	SpecialBoatRenderer SKULL = new SpecialSkullBlockRenderer();
	
	static @NotNull SpecialBoatRenderer get(@NotNull BlockState state) {
		//Beds, always rotated to face inside the boat. The BlockEntityRenderer takes care of drawing both halves of the bed
		if(state.getBlock() instanceof BedBlock) {
			return (boat, ext, yaw, pt, pose, bufs, light, state_, stack) -> {
				pose.mulPose(Vector3f.YP.rotationDegrees(180));
				DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_, stack);
			};
		}
		
		//Most double-height blocks, like doors and big flowers
		if(state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
			return (boat, ext, yaw, pt, pose, bufs, light, state_, stack) -> {
				DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_, stack);
				pose.translate(0, 1, 0);
				DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), stack);
			};
		}
		
		//Haxx for chests
		if(state.is(Blocks.CHEST)) return CHEST;
		if(state.is(Blocks.ENDER_CHEST)) return ENDER_CHEST;
		if(state.is(Blocks.LECTERN)) return LECTERN;
		if(state.getBlock() instanceof BannerBlock) return BANNER;
		if(state.getBlock() instanceof SkullBlock) return SKULL;
		
		//Haxx for certain block entities like enderchests; renderSingleBlock doesn't apply the rotation from the blockstate
		if(state.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return (boat, ext, yaw, pt, pose, bufs, light, state_, stack) -> {
				pose.mulPose(Vector3f.YP.rotationDegrees(-state_.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
				DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_, stack);
			};
		}
		
		//Everything else
		else return DEFAULT;
	}
}