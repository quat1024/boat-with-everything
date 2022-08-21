package agency.highlysuspect.boatwitheverything.special;

import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.container.ContainerExt;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpecialLecternRules implements SpecialBoatRules {
	@Override
	public @NotNull InteractionResult interact(Boat boat, BoatExt ext, Player player, InteractionHand hand) {
		BlockState state = ext.getBlockState();
		if(state == null || !state.hasProperty(BlockStateProperties.HAS_BOOK)) return InteractionResult.PASS;
		boolean hasBook = state.getValue(BlockStateProperties.HAS_BOOK);
		
		if(!(ext.getContainer() instanceof LecternContainerExt lec)) return InteractionResult.PASS;
		
		//If the player is holding a book let them add the book to the boat
		ItemStack held = player.getItemInHand(hand);
		if(!hasBook && held.is(ItemTags.LECTERN_BOOKS)) {
			lec.setBook(held.split(1));
			boat.playSound(SoundEvents.BOOK_PUT);
			return InteractionResult.SUCCESS;
		}
		
		//else try to open the book ui
		MenuProvider provider = getMenuProvider(boat, ext, player);
		if(provider != null) {
			boat.gameEvent(GameEvent.BLOCK_OPEN);
			player.openMenu(provider);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public boolean hasServerControlledInventory(Boat boat, BoatExt ext, Player player) {
		BlockState state = ext.getBlockState();
		if(state == null || !state.hasProperty(BlockStateProperties.HAS_BOOK)) return false;
		return state.getValue(BlockStateProperties.HAS_BOOK);
	}
	
	@Override
	public @Nullable ContainerExt makeNewContainer(Boat boat, BoatExt ext) {
		return new LecternContainerExt(boat, ext);
	}
	
	public static class LecternContainerExt implements ContainerExt {
		public LecternContainerExt(Boat boat, BoatExt ext) {
			this.boat = boat;
			this.ext = ext;
		}
		
		protected final Boat boat;
		protected final BoatExt ext;
		
		ItemStack book = ItemStack.EMPTY;
		int page = 0;
		final ContainerData pageData = new ContainerData() {
			@Override
			public int get(int id) {
				return id == 0 ? page : 0;
			}
			
			@Override
			public void set(int id, int payload) {
				if(id == 0) {
					int oldPage = page;
					page = payload;
					if(page != oldPage) {
						boat.playSound(SoundEvents.BOOK_PAGE_TURN, 1, boat.level.random.nextFloat() * 0.1f + 0.9f);
					}
				}
			}
			
			@Override
			public int getCount() {
				return 1;
			}
		};
		
		private void setBook(ItemStack stack) {
			book = stack.copy();
			page = 0;
			
			BlockState state = ext.getBlockState();
			if(state != null && state.hasProperty(BlockStateProperties.HAS_BOOK)) {
				ext.setBlockState(state.setValue(BlockStateProperties.HAS_BOOK, true));
			}
		}
		
		private void removeBook() {
			book = ItemStack.EMPTY;
			page = 0;
			
			BlockState state = ext.getBlockState();
			if(state != null && state.hasProperty(BlockStateProperties.HAS_BOOK)) {
				ext.setBlockState(state.setValue(BlockStateProperties.HAS_BOOK, false));
			}
		}
		
		@Override
		public NonNullList<ItemStack> getItemStacks() {
			return NonNullList.of(book);
		}
		
		@Override
		public int getContainerSize() {
			return 1;
		}
		
		@Override
		public boolean isEmpty() {
			return book.isEmpty();
		}
		
		@Override
		public ItemStack getItem(int slot) {
			return slot == 0 ? book : ItemStack.EMPTY;
		}
		
		@Override
		public ItemStack removeItem(int slot, int amt) {
			if(slot != 0) return ItemStack.EMPTY;
			ItemStack split = book.split(amt);
			if(book.isEmpty()) removeBook();
			return split;
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int i) {
			return removeItem(i, book.getCount());
		}
		
		@Override
		public void setItem(int i, ItemStack itemStack) {
			//No
		}
		
		@Override
		public int getMaxStackSize() {
			return 1;
		}
		
		@Override
		public void setChanged() {
			//No
		}
		
		@Override
		public boolean stillValid(Player player) {
			BlockState state = ext.getBlockState();
			return state != null && state.hasProperty(BlockStateProperties.HAS_BOOK) && state.getValue(BlockStateProperties.HAS_BOOK) && new SpecialContainerlessMenuRules.WeirdBoatContainerLevelAccess(boat).stillValid(player, b -> true);
		}
		
		@Override
		public void clearContent() {
			//No
		}
		
		@Override
		public boolean canPlaceItem(int slot, ItemStack stack) {
			return false; //No
		}
		
		@Override
		public CompoundTag writeSaveData() {
			CompoundTag tag = new CompoundTag();
			tag.put("Book", book.save(new CompoundTag()));
			tag.putInt("Page", page);
			return tag;
		}
		
		@Override
		public void readSaveData(CompoundTag tag) {
			book = ItemStack.of(tag.getCompound("Book"));
			page = tag.getInt("Page");
		}
		
		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
			BlockState state = ext.getBlockState();
			if(state == null || !state.hasProperty(BlockStateProperties.HAS_BOOK) || !state.getValue(BlockStateProperties.HAS_BOOK)) {
				return null;
			}
			
			return new LecternMenu(i, this, pageData);
		}
	}
}
