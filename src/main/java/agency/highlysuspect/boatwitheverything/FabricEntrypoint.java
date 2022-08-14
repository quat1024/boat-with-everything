package agency.highlysuspect.boatwitheverything;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@SuppressWarnings({"Convert2Lambda", "InstantiationOfUtilityClass"})
public class FabricEntrypoint implements ModInitializer {
	@Override
	public void onInitialize() {
		BoatWithEverything.INSTANCE = new BoatWithEverything(new LoaderServices() {
			@Override
			public void registerMenuType(ResourceLocation id, MenuType<? extends AbstractContainerMenu> type) {
				Registry.register(Registry.MENU, id, type);
			}
		});
	}
}
