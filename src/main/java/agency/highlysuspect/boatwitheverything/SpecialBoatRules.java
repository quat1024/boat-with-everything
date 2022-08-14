package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.special.SpecialDoorRules;
import agency.highlysuspect.boatwitheverything.special.SpecialLampRules;
import agency.highlysuspect.boatwitheverything.special.SpecialSpongeRules;
import agency.highlysuspect.boatwitheverything.special.SpecialTntRules;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface SpecialBoatRules {
	default boolean consumesPassengerSlot() {
		return true;
	}
	
	default void tick(Boat boat, BoatExt ext) {
		//no-op
	}
	
	default InteractionResult interact(Boat boat, BoatExt ext) {
		return InteractionResult.PASS;
	}
	
	SpecialBoatRules DEFAULT = new SpecialBoatRules() {};
	SpecialBoatRules DEFAULT_NO_CONSUME = new SpecialBoatRules() {
		@Override
		public boolean consumesPassengerSlot() {
			return false;
		}
	};
	
	SpecialBoatRules SPONGE = new SpecialSpongeRules();
	//dispenser (working w/ inventory)
	//note block (sound based on what block is below the boat)
	//furnace, blast furnace, smoker (gui, speed boost when operating)
	SpecialBoatRules TNT = new SpecialTntRules();
	//torch, soul torch, glowstone, shroomlight, light, jack o lantern, candle, candle cake, froglights (light source)
	//chest (vanilla overlap!)
	//crafting table (open crafting gui)
	//signs (placed like the banner, open sign UI in the inventory)
	SpecialBoatRules DOOR = new SpecialDoorRules(SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.IRON_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.IRON_DOOR_CLOSE);
	//cake (edible)
	SpecialBoatRules TRAPDOOR = new SpecialDoorRules(SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_CLOSE);
	SpecialBoatRules FENCE_GATE = new SpecialDoorRules(SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_CLOSE);
	//enchanting table (working, uses bookshelves from the world)
	//brewing stand (working)
	//cauldron (working interaction)
	SpecialBoatRules LAMP = new SpecialLampRules();
	//ender chest (working)
	//beacon (working?)
	//flower pot (working interaction)
	//dragon head (animation when powered)
	//dropper (working w/ inventory gui)
	//shulker box (working w/ animation and sound when opened)
	//conduit? maybe
	//loom, smithing table, cartography table, stonecutter (gui)
	//barrel (working w/ blockstate change and sound when opened)
	//bell (ding)
	//campfire, soul campfire (particles, maybe cook food)
	//lectern (other people can click to see the book lol)
	
	static SpecialBoatRules get(BlockState state) {
		if(state.is(BlockTags.BANNERS) || state.is(BlockTags.WOOL_CARPETS)) {
			return DEFAULT_NO_CONSUME;
		}
		
		if(state.is(Blocks.SPONGE)) return SPONGE;
		if(state.is(Blocks.TNT)) return TNT;
		
		if(state.hasProperty(BlockStateProperties.POWERED) && state.hasProperty(BlockStateProperties.OPEN)) {
			if(state.is(BlockTags.DOORS)) return DOOR;
			if(state.is(BlockTags.TRAPDOORS)) return TRAPDOOR;
			if(state.is(BlockTags.FENCE_GATES)) return FENCE_GATE;
		}
		
		if(state.hasProperty(BlockStateProperties.LIT) && state.is(Blocks.REDSTONE_LAMP)) {
			return LAMP;
		}
		
		return DEFAULT;
	}
	
	static boolean isPowered(Boat boat) {
		return BlockPos.betweenClosedStream(boat.getBoundingBox()).anyMatch(pos -> boat.level.hasNeighborSignal(pos));
	}
}
