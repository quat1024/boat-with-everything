package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.special.SpecialContainerlessMenuRules;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(AbstractContainerMenu.class)
public class MixinAbstractContainerMenu {
	@Inject(method = "stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z", at = @At("HEAD"), cancellable = true)
	private static void whenCheckingStillValid(ContainerLevelAccess containerLevelAccess, Player player, Block block, CallbackInfoReturnable<Boolean> cir) {
		if(containerLevelAccess instanceof SpecialContainerlessMenuRules.WeirdBoatContainerLevelAccess weird) {
			cir.setReturnValue(weird.stillValid(player, Predicate.isEqual(block)));
		}
	}
}
