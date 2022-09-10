package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.backport1_18.MyCustomInventoryScreen;
import agency.highlysuspect.boatwitheverything.container.ContainerExt;
import agency.highlysuspect.boatwitheverything.special.BoatRules;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Boat.class)
public abstract class MixinBoat_ContainerEntity implements Container, MyCustomInventoryScreen {
	// Grounding //
	
	@Override
	public boolean stillValid(Player player) {
		return container() != null && !boat().isRemoved() && boat().distanceToSqr(player) < 64;
	}
	
	// Menu //
	
	@Override
	public void openCustomInventoryScreen(Player player) {
		BoatRules rules = rules();
		if(rules == null) return;
		
		MenuProvider provider = rules.getMenuProvider(boat(), ext(), player);
		if(provider == null) return;
		
		player.openMenu(provider);
		if(!player.level.isClientSide) {
			boat().gameEvent(GameEvent.BLOCK_OPEN);
			PiglinAi.angerNearbyPiglins(player, true); //i suppose
		}
	}
	
	// Inventory //
	
	@Override
	public int getContainerSize() {
		ContainerExt cont = container();
		return cont == null ? 0 : cont.getContainerSize();
	}
	
	@Override
	public ItemStack getItem(int slot) {
		ContainerExt cont = container();
		return cont == null ? ItemStack.EMPTY : cont.getItem(slot);
	}
	
	@Override
	public ItemStack removeItem(int slot, int count) {
		ContainerExt cont = container();
		return cont == null ? ItemStack.EMPTY : cont.removeItem(slot, count);
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ContainerExt cont = container();
		return cont == null ? ItemStack.EMPTY : cont.removeItemNoUpdate(slot);
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
		ContainerExt cont = container();
		if(cont != null) cont.setItem(slot, stack);
	}
	
	@Override
	public void setChanged() {
		ContainerExt cont = container();
		if(cont != null) cont.setChanged();
	}
	
	@Override
	public void clearContent() {
		ContainerExt cont = container();
		if(cont != null) cont.clearContent();
	}
	
	@Override
	public int getMaxStackSize() {
		ContainerExt cont = container();
		return cont == null ? Container.LARGE_MAX_STACK_SIZE : cont.getMaxStackSize();
	}
	
	@Override
	public void startOpen(Player player) {
		ContainerExt cont = container();
		if(cont != null) cont.startOpen(player);
	}
	
	@Override
	public void stopOpen(Player player) {
		ContainerExt cont = container();
		if(cont != null) cont.stopOpen(player);
	}
	
	@Override
	public boolean isEmpty() {
		ContainerExt cont = container();
		return cont == null || cont.isEmpty();
	}
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		ContainerExt cont = container();
		return cont == null || cont.canPlaceItem(slot, stack);
	}
	
	// helpers //
	
	@Unique private Boat boat() {
		return (Boat) (Object) this;
	}
	
	@Unique private BoatExt ext() {
		return ((BoatDuck) this).bwe$getExt();
	}
	
	@Unique private @Nullable BoatRules rules() {
		return ext().getRules();
	}
	
	@Unique private @Nullable ContainerExt container() {
		return ext().getContainer();
	}
}
