package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.BoatDuck;
import agency.highlysuspect.boatwitheverything.BoatExt;
import agency.highlysuspect.boatwitheverything.ContainerExt;
import agency.highlysuspect.boatwitheverything.SpecialBoatRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode {
	@Inject(method = "isServerControlledInventory", at = @At("HEAD"), cancellable = true)
	public void whenCheckingServerControlledInventory(CallbackInfoReturnable<Boolean> cir) {
		//Boats have HasCustomInventoryScreen implemented unconditionally with MixinBoat_ContainerEntity.
		//We need to check that the boat actually requires a custom inventory right now, instead of using an instanceof check.
		//This is called from Minecraft#handleKeybinds. If "true" is returned, a packet is sent to *request* opening the inventory
		//via HasCustomInventoryScreen, instead of instantly opening the regular InventoryScreen on the client.
		LocalPlayer player = Minecraft.getInstance().player;
		assert player != null;
		
		if(player.isPassenger() && player.getVehicle() instanceof Boat boat) {
			ContainerExt containerExt = ((BoatDuck) boat).bwe$getExt().getContainer();
			if(containerExt == null) cir.setReturnValue(false);
			else cir.setReturnValue(containerExt.hasServerControlledInventory());
		}
	}
}
