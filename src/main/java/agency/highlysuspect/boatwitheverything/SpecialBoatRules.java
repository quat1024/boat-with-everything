package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.special.SpecialBarrelRules;
import agency.highlysuspect.boatwitheverything.special.SpecialCampfireRules;
import agency.highlysuspect.boatwitheverything.special.SpecialChestRules;
import agency.highlysuspect.boatwitheverything.special.SpecialConcretePowderRules;
import agency.highlysuspect.boatwitheverything.special.SpecialContainerlessMenuRules;
import agency.highlysuspect.boatwitheverything.special.SpecialDispenserRules;
import agency.highlysuspect.boatwitheverything.special.SpecialDoorRules;
import agency.highlysuspect.boatwitheverything.special.SpecialDropperRules;
import agency.highlysuspect.boatwitheverything.special.SpecialEnderChestRules;
import agency.highlysuspect.boatwitheverything.special.SpecialFlowerPotRules;
import agency.highlysuspect.boatwitheverything.special.SpecialLampRules;
import agency.highlysuspect.boatwitheverything.special.SpecialNoteBlockRules;
import agency.highlysuspect.boatwitheverything.special.SpecialSpongeRules;
import agency.highlysuspect.boatwitheverything.special.SpecialTntRules;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpecialBoatRules {
	default boolean consumesPassengerSlot() {
		return true;
	}
	
	default boolean isHeavy() {
		return false;
	}
	
	default void tick(Boat boat, BoatExt ext) {
		//no-op by default
	}
	
	default @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {
		MenuProvider provider = getMenuProvider(boat, ext, player);
		if(provider != null) {
			boat.gameEvent(GameEvent.BLOCK_OPEN);
			player.openMenu(provider);
		}
		return InteractionResult.SUCCESS;
	}
	
	//returning true will cancel further vanilla damage handling
	default boolean hurt(Boat boat, BoatExt ext, DamageSource source) {
		return false;
	}
	
	default int light(Boat boat, BoatExt ext) {
		BlockState state = ext.getBlockState();
		return state != null ? state.getLightEmission() : 0;
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
	SpecialBoatRules DEFAULT_HEAVY = new SpecialBoatRules() {
		@Override
		public boolean isHeavy() {
			return true;
		}
	};
	
	//beacon (working?)
	//shulker box (working w/ animation and sound when opened)
	//conduit? maybe
	//bell (ding)
	//lectern (other people can click to see the book lol)
	//furnace, smoker, blastfurnace (working, speed boost the boat when lit)
	//bed (set spawn? or maybe just differnet pose while riding? idk)
	
	//TODO TOODOOO big TODO
	// uhh make this a map instead of a linear scan lol
	static @NotNull SpecialBoatRules get(@NotNull BlockState state) {
		//chests and containers
		if(state.is(Blocks.BARREL)) return new SpecialBarrelRules();
		if(state.is(Blocks.CHEST)) return new SpecialChestRules();
		if(state.is(Blocks.ENDER_CHEST)) return new SpecialEnderChestRules();
		if(state.is(Blocks.DROPPER)) return new SpecialDropperRules();
		if(state.is(Blocks.DISPENSER)) return new SpecialDispenserRules();
		
		//fairly basic guis that can simply be opened server+client side with not much extra work
		//make sure to check that the boat ContainerLevelAccess can actually work safely for this gui (e.g. no setBlockState)
		if(state.is(Blocks.LOOM)) return new SpecialContainerlessMenuRules(LoomMenu::new);
		if(state.is(Blocks.CARTOGRAPHY_TABLE)) return new SpecialContainerlessMenuRules(CartographyTableMenu::new);
		if(state.is(Blocks.STONECUTTER)) return new SpecialContainerlessMenuRules(StonecutterMenu::new);
		if(state.is(Blocks.CRAFTING_TABLE)) return new SpecialContainerlessMenuRules(CraftingMenu::new);
		if(state.is(Blocks.GRINDSTONE)) return new SpecialContainerlessMenuRules(GrindstoneMenu::new);
		//(this one's a bit odd, see MixinItemCombinerMenu)
		if(state.is(Blocks.SMITHING_TABLE)) return new SpecialContainerlessMenuRules(SmithingMenu::new);
		
		//flower momer
		if(state.is(Blocks.FLOWER_POT)) return new SpecialFlowerPotRules.Unpotted();
		else if(state.is(BlockTags.FLOWER_POTS)) return new SpecialFlowerPotRules.Potted();
		
		//conk crete (no block tag Smh)
		if(state.getBlock() instanceof ConcretePowderBlock) return new SpecialConcretePowderRules();
		
		//banners
		if(state.is(BlockTags.BANNERS) || state.is(BlockTags.WOOL_CARPETS)) return DEFAULT_NO_CONSUME;
		
		//more weird blocks
		if(state.is(Blocks.SPONGE)) return new SpecialSpongeRules();
		if(state.is(Blocks.TNT)) return new SpecialTntRules();
		if(state.is(Blocks.REDSTONE_LAMP)) return new SpecialLampRules();
		if(state.is(Blocks.NOTE_BLOCK)) return new SpecialNoteBlockRules();
		if(state.is(BlockTags.CAMPFIRES)) return new SpecialCampfireRules();
		
		//doors and the like
		if(state.is(BlockTags.DOORS))
			return new SpecialDoorRules(SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.IRON_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.IRON_DOOR_CLOSE);
		if(state.is(BlockTags.TRAPDOORS))
			return new SpecialDoorRules(SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_CLOSE);
		if(state.is(BlockTags.FENCE_GATES))
			return new SpecialDoorRules(SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_CLOSE);
		
		//lol
		//BEACON_BASE_BLOCKS is a decent approximation for "metal storage blocks" btw
		if(state.is(Blocks.ANVIL) || state.is(Blocks.BEDROCK) || state.is(BlockTags.BEACON_BASE_BLOCKS)) return DEFAULT_HEAVY;
		
		//and everything else
		return DEFAULT;
	}
	
	static boolean isPowered(Boat boat) {
		for(Entity ent : boat.getPassengers()) {
			if(!(ent instanceof Player player)) continue;
			for(ItemStack held : player.getHandSlots()) if(held.getItem() == Items.REDSTONE_TORCH || held.getItem() == Items.REDSTONE_BLOCK) return true;
		}
		
		return BlockPos.betweenClosedStream(boat.getBoundingBox()).anyMatch(pos -> boat.level.hasNeighborSignal(pos));
	}
	
	static Vec3 positionOfBlock(Boat boat) {
		//Just vibes man idk why the magic numbers are there but it be like that
		double yaw = Math.toRadians(boat.getYRot() - 90);
		return boat.position().add(new Vec3(Math.cos(yaw), 2/16d, Math.sin(yaw)).scale(15/32d));
	}
}
