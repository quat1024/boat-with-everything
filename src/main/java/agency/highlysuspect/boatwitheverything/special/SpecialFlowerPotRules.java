package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import agency.highlysuspect.boatwitheverything.mixin.AccessorFlowerPotBlock;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class SpecialFlowerPotRules {
	public static class Potted implements SpecialBoatRules {
		@Override
		public @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {
			//check that we are a potted flower in the first place
			BlockState boatState = ext.getBlockState();
			if(!(boatState.getBlock() instanceof AccessorFlowerPotBlock afpb)) return InteractionResult.PASS;
			Block potted = afpb.bwe$content();
			if(potted == null) return InteractionResult.PASS;
			
			//empty the flower pot
			ext.setBlockState(Blocks.FLOWER_POT.defaultBlockState());
			ext.setItemStack(new ItemStack(Blocks.FLOWER_POT));
			
			//give the previous contents to the player, game events etc
			ItemStack yeah = new ItemStack(potted);
			if(!player.addItem(yeah)) player.drop(yeah, false);
			boat.level.gameEvent(player, GameEvent.BLOCK_CHANGE, boat.position());
			
			return InteractionResult.SUCCESS;
		}
		
		//TODO if you break or otherwise cause the boat to lose its contents
		// this kills the pot. All u get is the flower
	}
	
	public static class Unpotted implements SpecialBoatRules {
		@Override
		public @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {
			//check that we are a flower pot in the first place
			BlockState boatState = ext.getBlockState();
			if(!(boatState.getBlock() instanceof AccessorFlowerPotBlock afpb)) return InteractionResult.PASS;
			
			//check that the player is holding something
			ItemStack held = player.getItemInHand(hand);
			if(held.isEmpty()) return InteractionResult.PASS;
			
			//check that it's a block
			Item heldItem = held.getItem();
			if(!(heldItem instanceof BlockItem bi)) return InteractionResult.PASS;
			Block heldBlock = bi.getBlock();
			
			//check that there is a corresponding flowerpot block for that block
			Block resultBlock = AccessorFlowerPotBlock.bwe$getPottedByContent().get(heldBlock);
			if(resultBlock == null) return InteractionResult.PASS;
			
			//done! place the item in the boat
			ext.setBlockState(resultBlock.defaultBlockState());
			if(player.getAbilities().instabuild) {
				ItemStack heldCopy = held.copy();
				heldCopy.setCount(1);
				ext.setItemStack(heldCopy);
			} else {
				ext.setItemStack(held.split(1));
			}
			player.awardStat(Stats.POT_FLOWER);
			boat.level.gameEvent(player, GameEvent.BLOCK_CHANGE, boat.position());
			
			return InteractionResult.SUCCESS;
		}
	}
}
