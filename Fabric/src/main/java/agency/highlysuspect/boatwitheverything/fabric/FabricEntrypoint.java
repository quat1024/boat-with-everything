package agency.highlysuspect.boatwitheverything.fabric;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.LoaderServices;
import agency.highlysuspect.boatwitheverything.WeirdBlockRegistryThing;
import agency.highlysuspect.boatwitheverything.special.BoatRules;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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
			
			@Override
			public <T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(Factory<T> factory, Block... blocks) {
				return FabricBlockEntityTypeBuilder.create(factory::create).addBlocks(blocks).build();
			}
			
			@Override
			public void addMoreRules(WeirdBlockRegistryThing<BoatRules> rules) {
				if(FabricLoader.getInstance().isModLoaded("kahur")) {
					try {
						//I might be too cautious about classloading here
						Class.forName("agency.highlysuspect.boatwitheverything.fabric.integration.kahur.KahurIntegration")
							.getDeclaredMethod("addMoreRules", WeirdBlockRegistryThing.class)
							.invoke(null, rules);
					} catch (ReflectiveOperationException e) {
						//Oh well
						e.printStackTrace();
					}
				}
			}
		});
	}
}
