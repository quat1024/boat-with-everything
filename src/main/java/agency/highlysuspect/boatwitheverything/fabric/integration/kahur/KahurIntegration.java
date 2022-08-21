package agency.highlysuspect.boatwitheverything.fabric.integration.kahur;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.WeirdBlockRegistryThing;
import agency.highlysuspect.boatwitheverything.container.ContainerExt;
import agency.highlysuspect.boatwitheverything.special.BoatRules;
import com.mojang.math.Vector3f;
import com.unascribed.kahur.Kahur;
import com.unascribed.kahur.content.block.ConfettiCannonBlock;
import com.unascribed.kahur.init.KBlocks;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class KahurIntegration {
	public static void addMoreRules(WeirdBlockRegistryThing<BoatRules> rules) {
		rules.putBlock(new SpecialConfettiCannonRules(), KBlocks.CONFETTI_CANNON);
	}
	
	public static class SpecialConfettiCannonRules implements BoatRules {
		@Override
		public void tick(Boat boat, BoatExt ext) {
			BlockState state = ext.getBlockState();
			if(state == null || state.getBlock() != KBlocks.CONFETTI_CANNON) return;
			
			boolean isPowered = state.getValue(BlockStateProperties.TRIGGERED);
			boolean shouldPower = BoatRules.isPowered(boat);
			if(isPowered != shouldPower) {
				ext.setBlockState(state.setValue(BlockStateProperties.TRIGGERED, shouldPower));
				if(shouldPower && ext.getContainer() instanceof ConfettiCannonContainerExt ccce) {
					ccce.doot();
				}
			}
		}
		
		@Override
		public @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
			return new ConfettiCannonContainerExt(boat, ext);
		}
		
		public static class ConfettiCannonContainerExt extends ContainerExt.SimpleContainerImpl {
			public ConfettiCannonContainerExt(Boat boat, BoatExt ext) {
				super(boat, ext, 9);
			}
			
			public void doot() {
				BlockState state = ext.getBlockState();
				if(state == null) return;
				
				//Retyped version of ConfettiCannonBlockEntity#fire in Kahur.
				//Uses my own inventory, play sounds etc at the boat instead, u know the drill by now.
				
				//For the fancy nextGaussian that Minecraft's RNG doesn't have
				ThreadLocalRandom theCoolerDaniel = ThreadLocalRandom.current();
				
				//get colors
				IntArraySet colorSet = new IntArraySet();
				for(int i = 0; i < getContainerSize(); i++) {
					ItemStack stack = getItem(i);
					if(stack.getItem() instanceof DyeItem dye) {
						if(colorSet.add(dye.getDyeColor().getFireworkColor())) stack.shrink(1);
					} else if(stack.getItem() == Items.FIREWORK_STAR) {
						boolean hadColors = false;
						for(int color : stack.getOrCreateTagElement("Explosion").getIntArray("Colors")) {
							if(colorSet.add(color)) hadColors = true;
						}
						if(hadColors) stack.shrink(1);
					}
				}
				
				//toot or not toot
				if(colorSet.isEmpty()) {
					boat.playSound(SoundEvents.DISPENSER_FAIL, 1, 1.2f);
					return;
				}
				
				boat.playSound(SoundEvents.DISPENSER_DISPENSE);
				boat.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1, theCoolerDaniel.nextFloat(1.2f, 1.4f));
				
				if(!(boat.level instanceof ServerLevel slevel)) return;
				
				Vec3 unit = ConfettiCannonBlock.getUnitVector(state);
				//end copypaste for a bit. Need to calculate the real position of the confetti arm
				unit = unit.yRot(boat.getYRot() * -Mth.DEG_TO_RAD);
				Vector3f base = state.getValue(ConfettiCannonBlock.BASE).getOpposite().step();
				Vec3 pos = BoatRules.positionOfBlock(boat).add(0, 0.5, 0)
					.add(base.x() * 0.2, base.y() * 0.2, base.z() * 0.2);
				//begin copypaste again
				Vec3 vel = unit.add(
					theCoolerDaniel.nextGaussian(0, 0.05),
					theCoolerDaniel.nextGaussian(0, 0.05),
					theCoolerDaniel.nextGaussian(0, 0.05)
				);
				
				Packet<?> packet = Kahur.createConfettiPacket(pos, vel, colorSet.toIntArray());
				for(ServerPlayer player : slevel.getPlayers(p -> p.position().closerThan(pos, 64))) {
					if(player.connection != null) player.connection.send(packet);
				}
				
				//TODO: sync boat firing to the client as well so the renderer can play the proper animation (ticksSinceFired)
				// This means either a new HackyEntityUpdateId + MixinEntity_HackyBoatUpdates, which isn't pluggable for non classload safe stuff like this,
				// or finally get off my ass and write a real packet system lol
			}
			
			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
				return new DispenserMenu(i, inventory, this); //possibly better known as Generic3x3ContainerScreenHandler
			}
		}
	}
}
