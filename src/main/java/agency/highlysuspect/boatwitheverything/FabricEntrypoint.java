package agency.highlysuspect.boatwitheverything;

import net.fabricmc.api.ModInitializer;

@SuppressWarnings("InstantiationOfUtilityClass")
public class FabricEntrypoint implements ModInitializer {
	@Override
	public void onInitialize() {
		BoatWithEverything.INSTANCE = new BoatWithEverything();
	}
}
