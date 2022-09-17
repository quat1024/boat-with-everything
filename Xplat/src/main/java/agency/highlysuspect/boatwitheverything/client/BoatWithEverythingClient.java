package agency.highlysuspect.boatwitheverything.client;

import agency.highlysuspect.boatwitheverything.WeirdBlockRegistryThing;
import com.google.common.base.Suppliers;
import com.mojang.math.Vector3f;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.function.Supplier;

public class BoatWithEverythingClient {
	public static BoatWithEverythingClient INSTANCE;
	public final ClientLoaderServices services;
	
	public Supplier<WeirdBlockRegistryThing<SpecialBoatRenderer>> rendererRegistry = Suppliers.memoize(this::makeRendererRegistry);
	
	public BoatWithEverythingClient(ClientLoaderServices services) {
		this.services = services;
	}
	
	public WeirdBlockRegistryThing<SpecialBoatRenderer> makeRendererRegistry() {
		WeirdBlockRegistryThing<SpecialBoatRenderer> r = new WeirdBlockRegistryThing<>(SpecialBoatRenderer.DEFAULT);
		
		//Relatively mundane cases
		r.putBlock(SpecialBoatRenderer.CHEST, Blocks.CHEST);
		r.putBlock(SpecialBoatRenderer.ENDER_CHEST, Blocks.ENDER_CHEST);
		r.putBlock(SpecialBoatRenderer.LECTERN, Blocks.LECTERN);
		r.putClass(SpecialBoatRenderer.BANNER, BannerBlock.class);
		r.putClass(SpecialBoatRenderer.SKULL, SkullBlock.class);
		
		//Beds
		r.putBlockTag((boat, ext, yaw, pt, pose, bufs, light, state_, stack) -> {
			pose.mulPose(Vector3f.YP.rotationDegrees(180));
			SpecialBoatRenderer.DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_, stack);
		}, BlockTags.BEDS);
		
		//Double-tall blocks
		r.putSpecial((boat, ext, yaw, pt, pose, bufs, light, state_, stack) -> {
			SpecialBoatRenderer.DEFAULT.DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_, stack);
			pose.translate(0, 1, 0);
			SpecialBoatRenderer.DEFAULT.DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), stack);
		}, (state) -> state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER);
		
		//Idk if this one is still used lol?
		r.putSpecial((boat, ext, yaw, pt, pose, bufs, light, state_, stack) -> {
			pose.mulPose(Vector3f.YP.rotationDegrees(-state_.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
			SpecialBoatRenderer.DEFAULT.render(boat, ext, yaw, pt, pose, bufs, light, state_, stack);
		}, (state) -> state.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING));
		
		services.addMoreRenderers(r);
		
		return r;
	}
}
