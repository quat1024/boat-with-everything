package agency.highlysuspect.boatwitheverything.mixin;

import agency.highlysuspect.boatwitheverything.special.SpecialContainerlessMenuRules;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@SuppressWarnings("ConstantConditions")
@Mixin(ItemCombinerMenu.class)
public class MixinItemCombinerMenu {
	@Shadow @Final protected ContainerLevelAccess access;
	
	@Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
	private void whenCheckingStillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
		if(access instanceof SpecialContainerlessMenuRules.WeirdBoatContainerLevelAccess weird) {
			Predicate<Block> blockPred;
			if(((Object) this) instanceof SmithingMenu) blockPred = Predicate.isEqual(Blocks.SMITHING_TABLE);
			else if(((Object) this) instanceof AnvilMenu) blockPred = b -> b.defaultBlockState().is(BlockTags.ANVIL);
			else blockPred = b -> false;
			
			cir.setReturnValue(weird.stillValid(player, blockPred));
		}
	}
}
