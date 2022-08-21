package agency.highlysuspect.boatwitheverything.fabric;

import agency.highlysuspect.boatwitheverything.client.BoatWithEverythingClient;
import agency.highlysuspect.boatwitheverything.client.ClientLoaderServices;
import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BoatWithEverythingClient.INSTANCE = new BoatWithEverythingClient(new ClientLoaderServices() {
			
		});
	}
}
