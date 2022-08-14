package agency.highlysuspect.boatwitheverything;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.Predicate;

public class DelegatingContainer<T extends Container> implements Container {
	public DelegatingContainer(T delegate) {
		this.delegate = delegate;
	}
	
	protected final T delegate;
	
	@Override public int getContainerSize() {return delegate.getContainerSize();}
	@Override public boolean isEmpty() {return delegate.isEmpty();}
	@Override public ItemStack getItem(int i) {return delegate.getItem(i);}
	@Override public ItemStack removeItem(int i, int j) {return delegate.removeItem(i, j);}
	@Override public ItemStack removeItemNoUpdate(int i) {return delegate.removeItemNoUpdate(i);}
	@Override public void setItem(int i, ItemStack itemStack) {delegate.setItem(i, itemStack);}
	@Override public int getMaxStackSize() {return delegate.getMaxStackSize();}
	@Override public void setChanged() {delegate.setChanged();}
	@Override public boolean stillValid(Player player) {return delegate.stillValid(player);}
	@Override public void startOpen(Player player) {delegate.startOpen(player);}
	@Override public void stopOpen(Player player) {delegate.stopOpen(player);}
	@Override public boolean canPlaceItem(int i, ItemStack itemStack) {return delegate.canPlaceItem(i, itemStack);}
	@Override public int countItem(Item item) {return delegate.countItem(item);}
	@Override public boolean hasAnyOf(Set<Item> set) {return delegate.hasAnyOf(set);}
	@Override public boolean hasAnyMatching(Predicate<ItemStack> predicate) {return delegate.hasAnyMatching(predicate);}
	@Override public void clearContent() {delegate.clearContent();}
}
