package agency.highlysuspect.boatwitheverything.mixin.backport1_18;

import agency.highlysuspect.boatwitheverything.backport1_18.MyCustomInventoryScreen;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
	@Shadow public ServerPlayer player;
	
	@Inject(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V", shift = At.Shift.AFTER), cancellable = true)
	private void whenHandlingPlayerCommand(ServerboundPlayerCommandPacket cmd, CallbackInfo ci) {
		ServerboundPlayerCommandPacket.Action action = cmd.getAction();
		if(action == ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY && player.getVehicle() instanceof MyCustomInventoryScreen my) {
			my.openCustomInventoryScreen(player);
			ci.cancel();
		}
	}
}
