package agency.highlysuspect.boatwitheverything.forge;

import agency.highlysuspect.boatwitheverything.WeirdBlockRegistryThing;
import agency.highlysuspect.boatwitheverything.client.BoatWithEverythingClient;
import agency.highlysuspect.boatwitheverything.client.ClientLoaderServices;
import agency.highlysuspect.boatwitheverything.client.SpecialBoatRenderer;

public class ForgeClientNotEntrypoint {
	public ForgeClientNotEntrypoint() {
		BoatWithEverythingClient.INSTANCE = new BoatWithEverythingClient(new ClientLoaderServices() {
			@Override
			public void addMoreRenderers(WeirdBlockRegistryThing<SpecialBoatRenderer> r) {
				//Nope
			}
		});
	}
}
