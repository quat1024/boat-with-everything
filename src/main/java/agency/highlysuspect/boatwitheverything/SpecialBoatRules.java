package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.special.SpecialBarrelRules;
import agency.highlysuspect.boatwitheverything.special.SpecialDoorRules;
import agency.highlysuspect.boatwitheverything.special.SpecialLampRules;
import agency.highlysuspect.boatwitheverything.special.SpecialSpongeRules;
import agency.highlysuspect.boatwitheverything.special.SpecialTntRules;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface SpecialBoatRules {
	default boolean consumesPassengerSlot() {
		return true;
	}
	
	default void tick(Boat boat, BoatExt ext) {
		//no-op by default
	}
	
	default @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player) {
		ContainerExt cext = ext.getContainer();
		if(cext == null) return InteractionResult.PASS;
		
		boat.gameEvent(GameEvent.BLOCK_OPEN);
		player.openMenu(new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return boat.getDisplayName();
			}
			
			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int sequence, Inventory playerInventory, Player player) {
				return cext.createMenu(sequence, playerInventory, player);
			}
		});
		
		return InteractionResult.SUCCESS;
	}
	
	// additional data //
	
	String CONTAINER_KEY = "BoatWithEverything$container";
	
	default void addAdditionalSaveData(Boat boat, BoatExt ext, CompoundTag tag) {
		ContainerExt cext = ext.getContainer();
		if(cext != null) tag.put(CONTAINER_KEY, cext.writeSaveData());
	}
	
	default void readAdditionalSaveData(Boat boat, BoatExt ext, CompoundTag tag) {
		ContainerExt cext = ext.getContainer();
		if(cext != null && tag.contains(CONTAINER_KEY)) cext.readSaveData(tag.getCompound(CONTAINER_KEY));
	}
	
	// container utils //
	
	default @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		return null;
	}
	
	SpecialBoatRules DEFAULT = new SpecialBoatRules() {};
	SpecialBoatRules DEFAULT_NO_CONSUME = new SpecialBoatRules() {
		@Override
		public boolean consumesPassengerSlot() {
			return false;
		}
	};
	
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
	
	static @NotNull SpecialBoatRules get(@NotNull BlockState state) {
		if(state.is(Blocks.BARREL)) return new SpecialBarrelRules();
		
		if(state.is(BlockTags.BANNERS) || state.is(BlockTags.WOOL_CARPETS)) return DEFAULT_NO_CONSUME;
		
		if(state.is(Blocks.SPONGE)) return new SpecialSpongeRules();
		if(state.is(Blocks.TNT)) return new SpecialTntRules();
		
		if(state.hasProperty(BlockStateProperties.POWERED) && state.hasProperty(BlockStateProperties.OPEN)) {
			if(state.is(BlockTags.DOORS))
				return new SpecialDoorRules(SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.IRON_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.IRON_DOOR_CLOSE);
			if(state.is(BlockTags.TRAPDOORS))
				return new SpecialDoorRules(SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_CLOSE);
			if(state.is(BlockTags.FENCE_GATES))
				return new SpecialDoorRules(SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_CLOSE);
		}
		
		if(state.hasProperty(BlockStateProperties.LIT) && state.is(Blocks.REDSTONE_LAMP)) {
			return new SpecialLampRules();
		}
		
		return DEFAULT;
	}
	
	static boolean isPowered(Boat boat) {
		return BlockPos.betweenClosedStream(boat.getBoundingBox()).anyMatch(pos -> boat.level.hasNeighborSignal(pos));
	}
}
