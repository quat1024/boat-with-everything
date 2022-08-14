package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Boat.class)
public abstract class MixinBoat_ContainerEntity implements ContainerEntity, HasCustomInventoryScreen {
	// Grounding //
	
	@Override
	public boolean stillValid(Player player) {
		return isChestVehicleStillValid(player);
	}
	
	// Menu //
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int sequenceNumber, Inventory playerInventory, Player player) {
		ContainerExt containerExt = container();
		return containerExt == null ? null : containerExt.createMenu(sequenceNumber, playerInventory, player);
	}
	
	@Override
	public void openCustomInventoryScreen(Player player) {
		//idk
	}
	
	// Inventory //
	
	@Unique private static final NonNullList<ItemStack> EMPTY = NonNullList.withSize(0, ItemStack.EMPTY);
	
	@Override
	public NonNullList<ItemStack> getItemStacks() {
		ContainerExt cont = container();
		return cont == null ? EMPTY : cont.getItemStacks();
	}
	
	@Override
	public void clearItemStacks() {
		ContainerExt cont = container();
		if(cont != null) cont.clearContent();
	}
	
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
		clearItemStacks();
	}
	
	// Loot table junk //
	
	@Nullable
	@Override
	public ResourceLocation getLootTable() {
		return null;
	}
	
	@Override
	public void setLootTable(@Nullable ResourceLocation resourceLocation) {
		//nope
	}
	
	@Override
	public long getLootTableSeed() {
		return 0;
	}
	
	@Override
	public void setLootTableSeed(long l) {
		
	}
	
	// helpers //
	
	@Unique private Boat boat() {
		return (Boat) (Object) this;
	}
	
	@Unique private BoatExt ext() {
		return ((BoatDuck) this).bwe$getExt();
	}
	
	@Unique private @Nullable ContainerExt container() {
		return ext().getContainer();
	}
}
