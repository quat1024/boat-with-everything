package agency.highlysuspect.boatwitheverything;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface LoaderServices {
	void registerMenuType(ResourceLocation id, MenuType<? extends AbstractContainerMenu> type);
}
