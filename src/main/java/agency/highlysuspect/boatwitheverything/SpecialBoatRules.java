package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.special.SpecialBarrelRules;
import agency.highlysuspect.boatwitheverything.special.SpecialChestRules;
import agency.highlysuspect.boatwitheverything.special.SpecialContainerlessMenuRules;
import agency.highlysuspect.boatwitheverything.special.SpecialDoorRules;
import agency.highlysuspect.boatwitheverything.special.SpecialEnderChestRules;
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
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpecialBoatRules {
	default boolean consumesPassengerSlot() {
		return true;
	}
	
	default void tick(Boat boat, BoatExt ext) {
		//no-op by default
	}
	
	default @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player) {
		MenuProvider provider = getMenuProvider(boat, ext, player);
		if(provider != null) {
			boat.gameEvent(GameEvent.BLOCK_OPEN);
			player.openMenu(provider);
		}
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
	
	// container/menu utils //
	
	default @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		return null;
	}
	
	default boolean hasServerControlledInventory(Boat boat, BoatExt ext, Player player) {
		return getMenuProvider(boat, ext, player) != null;
	}
	
	default @Nullable MenuProvider getMenuProvider(Boat boat, BoatExt ext, Player player) {
		ContainerExt cext = ext.getContainer();
		if(cext != null) return new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return boat.getDisplayName();
			}
			
			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
				return cext.createMenu(i, inventory, player);
			}
		};
		else return null;
	}
	
	//////////
	
	SpecialBoatRules DEFAULT = new SpecialBoatRules() {};
	SpecialBoatRules DEFAULT_NO_CONSUME = new SpecialBoatRules() {
		@Override
		public boolean consumesPassengerSlot() {
			return false;
		}
	};
	
	//beacon (working?)
	//flower pot (working interaction)
	//dropper, maybe dispenser if its not too difficult (working w/ inventory gui)
	//shulker box (working w/ animation and sound when opened)
	//conduit? maybe
	//bell (ding)
	//note block (ding)
	//campfire, soul campfire (particles, maybe cook food)
	//lectern (other people can click to see the book lol)
	//torch, glowstone, soul torch, etc (light source that follows you around)
	//furnace, smoker, blastfurnace (working, speed boost the boat when lit)
	//grindstone (gui)
	//anvil (gui, needs haxx to avoid spawning an anvil block on damaging)
	//bed (set spawn? or maybe just differnet pose while riding? idk)
	
	static @NotNull SpecialBoatRules get(@NotNull BlockState state) {
		if(state.is(Blocks.BARREL)) return new SpecialBarrelRules();
		if(state.is(Blocks.CHEST)) return new SpecialChestRules();
		if(state.is(Blocks.ENDER_CHEST)) return new SpecialEnderChestRules();
		
		//fairly basic guis that can simply be opened server client side with not much extra work
		//make sure to check that the boat ContainerLevelAccess can actually work safely for this gui (e.g. no setBlockState)
		if(state.is(Blocks.LOOM)) return new SpecialContainerlessMenuRules(LoomMenu::new);
		if(state.is(Blocks.CARTOGRAPHY_TABLE)) return new SpecialContainerlessMenuRules(CartographyTableMenu::new);
		if(state.is(Blocks.STONECUTTER)) return new SpecialContainerlessMenuRules(StonecutterMenu::new);
		if(state.is(Blocks.CRAFTING_TABLE)) return new SpecialContainerlessMenuRules(CraftingMenu::new);
		//(this one's a bit odd, see MixinItemCombinerMenu)
		if(state.is(Blocks.SMITHING_TABLE)) return new SpecialContainerlessMenuRules(SmithingMenu::new);
		
		if(state.is(BlockTags.BANNERS) || state.is(BlockTags.WOOL_CARPETS)) return DEFAULT_NO_CONSUME;
		
		if(state.is(Blocks.SPONGE)) return new SpecialSpongeRules();
		if(state.is(Blocks.TNT)) return new SpecialTntRules();
		
		if(state.is(BlockTags.DOORS))
			return new SpecialDoorRules(SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.IRON_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.IRON_DOOR_CLOSE);
		if(state.is(BlockTags.TRAPDOORS))
			return new SpecialDoorRules(SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_CLOSE);
		if(state.is(BlockTags.FENCE_GATES))
			return new SpecialDoorRules(SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_CLOSE);
		
		if(state.is(Blocks.REDSTONE_LAMP)) return new SpecialLampRules();
		
		return DEFAULT;
	}
	
	static boolean isPowered(Boat boat) {
		return BlockPos.betweenClosedStream(boat.getBoundingBox()).anyMatch(pos -> boat.level.hasNeighborSignal(pos));
	}
}
