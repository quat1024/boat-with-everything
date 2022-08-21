package agency.highlysuspect.boatwitheverything.special;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.container.ContainerExt;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BoatRules {
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
	
	//////////
	
	BoatRules DEFAULT = new BoatRules() {};
}
