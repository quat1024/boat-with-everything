package agency.highlysuspect.boatwitheverything;

import agency.highlysuspect.boatwitheverything.block.BoatLightBlock;
import agency.highlysuspect.boatwitheverything.block.BoatLightBlockEntity;
import agency.highlysuspect.boatwitheverything.special.SpecialBarrelRules;
import agency.highlysuspect.boatwitheverything.special.BoatRules;
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
import agency.highlysuspect.boatwitheverything.special.SpecialLecternRules;
import agency.highlysuspect.boatwitheverything.special.SpecialNoteBlockRules;
import agency.highlysuspect.boatwitheverything.special.SpecialSpongeRules;
import agency.highlysuspect.boatwitheverything.special.SpecialTntRules;
import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BoatWithEverything {
	public static final String MODID = "boat-with-everything";
	public static BoatWithEverything INSTANCE;
	public static ThreadLocal<Boat> HOPPER_SKIP_THIS_BOAT_PLEASE = ThreadLocal.withInitial(() -> null); //see MixinEntitySelector and yes, its not pretty
	
	public LoaderServices services;
	
	public BoatLightBlock boatLightBlock;
	public BlockEntityType<BoatLightBlockEntity> boatLightBlockEntityType;
	
	public Supplier<WeirdBlockRegistryThing<BoatRules>> rulesRegistry = Suppliers.memoize(this::makeRulesRegistry);
	
	public BoatWithEverything(LoaderServices services) {
		this.services = services;
		
		services.blocks(reg -> {
			boatLightBlock = new BoatLightBlock();
			reg.register(id("light"), boatLightBlock);
		});
		
		services.blockEntityTypes(reg -> {
			boatLightBlockEntityType = FabricBlockEntityTypeBuilder.create(BoatLightBlockEntity::new, boatLightBlock).build();
			reg.register(id("light"), boatLightBlockEntityType);
		});
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> blockTickersCanKissMyAss(BlockEntityType<A> realType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> epic) {
		return expectedType == realType ? (BlockEntityTicker<A>) epic : null;
	}
	
	public WeirdBlockRegistryThing<BoatRules> makeRulesRegistry() {
		WeirdBlockRegistryThing<BoatRules> r = new WeirdBlockRegistryThing<>(BoatRules.DEFAULT);
		
		//TODO:
		// beacon (working?)
		// shulker box (working w/ animation and sound when opened)
		// conduit? maybe
		// bell (ding)
		// furnace, smoker, blastfurnace (working, speed boost the boat when lit)
		// bed (set spawn? or maybe just differnet pose while riding? idk)
		
		//special containers
		r.put(new SpecialBarrelRules(), Blocks.BARREL);
		r.put(new SpecialChestRules(), Blocks.CHEST);
		r.put(new SpecialEnderChestRules(), Blocks.ENDER_CHEST);
		r.put(new SpecialDropperRules(), Blocks.DROPPER);
		r.put(new SpecialDispenserRules(), Blocks.DISPENSER);
		r.put(new SpecialLecternRules(), Blocks.LECTERN);
		
		//generic containers
		r.put(new SpecialContainerlessMenuRules(LoomMenu::new), Blocks.LOOM);
		r.put(new SpecialContainerlessMenuRules(CartographyTableMenu::new), Blocks.CARTOGRAPHY_TABLE);
		r.put(new SpecialContainerlessMenuRules(StonecutterMenu::new), Blocks.STONECUTTER);
		r.put(new SpecialContainerlessMenuRules(CraftingMenu::new), Blocks.CRAFTING_TABLE);
		r.put(new SpecialContainerlessMenuRules(GrindstoneMenu::new), Blocks.GRINDSTONE);
		r.put(new SpecialContainerlessMenuRules(SmithingMenu::new), Blocks.SMITHING_TABLE); //see MixinAbstractContainerMenu
		
		//flower momer
		r.put(new SpecialFlowerPotRules.Unpotted(), Blocks.FLOWER_POT);
		r.put(new SpecialFlowerPotRules.Potted(), BlockTags.FLOWER_POTS);
		
		//conk crete
		r.put(new SpecialConcretePowderRules(), ConcretePowderBlock.class);
		
		//banners, carpets, and things that don't take up space
		r.putMixed(new BoatRules() {
			@Override
			public boolean consumesPassengerSlot() {
				return false;
			}
		}, BlockTags.BANNERS, BlockTags.WOOL_CARPETS, Blocks.DRAGON_HEAD);
		
		//doors
		r.put(SpecialDoorRules.DOORS, BlockTags.DOORS);
		r.put(SpecialDoorRules.TRAPDOORS, BlockTags.TRAPDOORS);
		r.put(SpecialDoorRules.FENCE_GATES, BlockTags.FENCE_GATES);
		
		//heavy blocks
		r.putMixed(new BoatRules() {
			@Override
			public boolean isHeavy() {
				return true;
			}
		}, Blocks.ANVIL, Blocks.BEDROCK, BlockTags.BEACON_BASE_BLOCKS);
		
		//odds and ends
		r.put(new SpecialSpongeRules(), Blocks.SPONGE);
		r.put(new SpecialTntRules(), Blocks.TNT);
		r.put(new SpecialLampRules(), Blocks.REDSTONE_LAMP);
		r.put(new SpecialNoteBlockRules(), Blocks.NOTE_BLOCK);
		r.put(new SpecialCampfireRules(), Blocks.CAMPFIRE);
		
		return r;
	}
	
	/////
	
	public boolean hurt(Boat boat, BoatExt ext, DamageSource source) {
		if(!ext.hasBlockState()) return false;
		
		BoatRules rules = ext.getRules();
		if(rules != null) {
			boolean handled = rules.hurt(boat, ext, source);
			if(handled) return true;
		}
		
		//return the item that was used to place the block in the boat
		ItemStack stackInBoat = ext.getItemStack().copy();
		@Nullable Player player = source.getDirectEntity() instanceof Player p ? p : null;
		
		@SuppressWarnings("SimplifiableConditionalExpression")
		boolean locked = player == null ? false : (ext.isLocked() && !player.getAbilities().instabuild);
		
		if(player != null && (locked || !player.mayInteract(boat.level, boat.blockPosition()))) {
			return locked; //if locked, dont even allow damaging the boat (for modfest)
		}
		
		if(player == null || !player.addItem(stackInBoat)) {
			boat.spawnAtLocation(stackInBoat, boat.getBbHeight());
		}
		
		//remove all the stuff from the boat
		ext.clearBlockState();
		ext.clearItemStack();
		
		boat.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM); //todo caption
		return player != null; //Only cancel vanilla damage handling if it was from a player punch.
	}
	
	public @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {		
		//Vanilla boat interaction always instantly returns when you're sneaking, so adding more behavior on sneak doesn't conflict
		if(!player.isSecondaryUseActive()) return InteractionResult.PASS;
		
		//If there's something in the boat already, perform its right click action
		@Nullable BoatRules rules = ext.getRules();
		if(rules != null && hand == InteractionHand.MAIN_HAND) {
			InteractionResult result = rules.interact(boat, ext, player, hand);
			if(result != InteractionResult.PASS) return result;
		}
		
		boolean locked = ext.isLocked() && !player.getAbilities().instabuild;
		ItemStack held = player.getItemInHand(hand);
		
		if(locked || !player.mayInteract(boat.level, boat.blockPosition()) || !player.mayUseItemAt(boat.blockPosition(), Direction.UP, held)) {
			return InteractionResult.PASS;
		}
		
		if(held.getItem() == Items.WATER_BUCKET) {
			ext.clickWithWaterBucket();
			
			player.setItemInHand(hand, BucketItem.getEmptySuccessItem(held, player));
			boat.level.playSound(player, boat, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1, 1);
			return InteractionResult.SUCCESS;
		}
		
		//If there's no blockstate, add it to the boat
		BlockState placementState;
		if((placementState = getPlacementStateInsideBoat(player, boat, hand)) != null && canAddBlockState(boat, ext, placementState)) {
			ext.setBlockState(placementState);
			ext.setItemStack(held.split(1));
			
			boat.playSound(placementState.getSoundType().getPlaceSound());
			
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	private @Nullable BlockState getPlacementStateInsideBoat(Player player, Boat boat, InteractionHand hand) {
		//Big TODO
		ItemStack stack = player.getItemInHand(hand);
		if(stack.getItem() instanceof BlockItem bi) {
			//Turn the player momentarily to fool anything using BlockPlaceContext#getDirection or similar
			float oldYRot = player.getYRot();
			float oldYHeadRot = player.getYHeadRot();
			float oldYRot0 = player.yRotO;
			
			float relativeDirection = Mth.wrapDegrees(player.getYRot() - boat.getYRot());
			player.setYRot(relativeDirection); //used by BlockPlaceContext#getDirection
			player.setYHeadRot(relativeDirection); //used by #getNearestLookingDirection but only on the server lol
			player.yRotO = relativeDirection; //idk cant hurt ?
			
			//Owo what's this? A leaky abstraction? Never seen that in this mod before
			BlockState state;
			if(bi.getBlock() instanceof ConcretePowderBlock) state = bi.getBlock().defaultBlockState();
			else state = bi.getBlock().getStateForPlacement(new BlockPlaceContext(
				player, hand, stack, 
				new BlockHitResult(boat.position(), Direction.UP, boat.blockPosition(), true)
			));
			
			//restore player position
			player.setYRot(oldYRot);
			player.setYHeadRot(oldYHeadRot);
			player.yRotO = oldYRot0;
			
			//return
			return state; 
		} else return null;
	}
	
	private boolean canAddBlockState(Boat boat, BoatExt ext, BlockState state) {
		if(boat instanceof ChestBoat || ext.hasBlockState()) return false;
		return boat.getPassengers().size() < ext.getMaxPassengers();
	}
}
