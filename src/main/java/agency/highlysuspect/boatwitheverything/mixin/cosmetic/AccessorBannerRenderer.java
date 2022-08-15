package agency.highlysuspect.boatwitheverything.mixin.cosmetic;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BannerRenderer.class)
public interface AccessorBannerRenderer {
	@Accessor("flag") ModelPart bwe$flag();
	@Accessor("pole") ModelPart bwe$pole();
	@Accessor("bar") ModelPart bwe$bar();
}
