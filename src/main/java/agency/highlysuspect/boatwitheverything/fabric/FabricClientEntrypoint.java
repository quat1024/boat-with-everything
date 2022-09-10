package agency.highlysuspect.boatwitheverything.fabric;

import agency.highlysuspect.boatwitheverything.WeirdBlockRegistryThing;
import agency.highlysuspect.boatwitheverything.client.BoatWithEverythingClient;
import agency.highlysuspect.boatwitheverything.client.ClientLoaderServices;
import agency.highlysuspect.boatwitheverything.client.SpecialBoatRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BoatWithEverythingClient.INSTANCE = new BoatWithEverythingClient(new ClientLoaderServices() {
			@Override
			public void addMoreRenderers(WeirdBlockRegistryThing<SpecialBoatRenderer> r) {
//				if(FabricLoader.getInstance().isModLoaded("kahur")) {
//					try {
//						//I might be too cautious about classloading here
//						Class.forName("agency.highlysuspect.boatwitheverything.fabric.integration.kahur.KahurIntegrationClient")
//							.getDeclaredMethod("addMoreRenderers", WeirdBlockRegistryThing.class)
//							.invoke(null, r);
//					} catch (ReflectiveOperationException e) {
//						//Oh well
//						e.printStackTrace();
//					}
//				}
			}
		});
	}
}
