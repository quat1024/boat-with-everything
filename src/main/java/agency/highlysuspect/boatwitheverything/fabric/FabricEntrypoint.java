package agency.highlysuspect.boatwitheverything.fabric;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.LoaderServices;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;

public class FabricEntrypoint implements ModInitializer {
	@Override
	public void onInitialize() {
		BoatWithEverything.INSTANCE = new BoatWithEverything(new LoaderServices() {
			private <T> Registerer<T> vanillaRegisterer(Registry<T> r) {
				return (id, thing) -> Registry.register(r, id, thing);
			}
			
			@Override
			public void blocks(Consumer<Registerer<Block>> reg) {
				reg.accept(vanillaRegisterer(Registry.BLOCK));
			}
			
			@Override
			public void blockEntityTypes(Consumer<Registerer<BlockEntityType<?>>> reg) {
				reg.accept(vanillaRegisterer(Registry.BLOCK_ENTITY_TYPE));
			}
		});
	}
}
