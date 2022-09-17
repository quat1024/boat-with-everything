package agency.highlysuspect.boatwitheverything.forge;

import agency.highlysuspect.boatwitheverything.BoatWithEverything;
import agency.highlysuspect.boatwitheverything.LoaderServices;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Consumer;

@Mod(ForgeEntrypoint.I_AM_SCARED_OF_HYPHENS)
public class ForgeEntrypoint {
	/**
	 * Quoting the Forge documentation for `@Mod#value`:
	 * 
	 *    """"
	 *    This will be used to identify your mod for third parties (other mods), it will
	 *    be used to identify your mod for registries such as block and item registries.
	 *    By default, you will have a resource domain that matches the modid. All these
	 *    uses require that constraints are imposed on the format of the modid.
	 *    """
	 * 
	 * I mean it's vague on what the "constraints" imposed are, but I don't think it's unreasonable
	 * to read that as "the modid must be a valid resource domain". If that's what they meant, it's
	 * wrong: hyphens are a valid ResourceLocation domain, but Forge shits itself when encountering one in a modid.
	 * It's too late to change the modid on Fabric because there are already blocks existing in people's worlds.
	 * So I will instead use a different modid on forge and fabric. This will certainly not come back
	 * around to bite me in the tail later.
	 * 
	 * Fortunately Forge allows modders to override the namespace associated with their mod with a
	 * `namespace =` line in mods.toml. The validation on that is still not correct, because the length
	 * is capped and a hyphen cannot be the first character, but it at least allows hyphens at all.
	 * 
	 * It strikes me that Forge would not need this namespace override feature at all if they
	 * just validated modids correctly, but whatever.
	 */
	public static final String I_AM_SCARED_OF_HYPHENS = "boat_with_everything";
	
	public ForgeEntrypoint() {
		BoatWithEverything.INSTANCE = new BoatWithEverything(new LoaderServices() {
			@Override
			public void blocks(Consumer<Registerer<Block>> reg) {
				//serious modloader
				FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent what) -> what.register(Registry.BLOCK_REGISTRY, h -> reg.accept(h::register)));
			}
			
			@Override
			public void blockEntityTypes(Consumer<Registerer<BlockEntityType<?>>> reg) {
				//good modloader that i need to take seriously apparently
				FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterEvent what) -> what.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY, h -> reg.accept(h::register)));
			}
			
			@SuppressWarnings("ConstantConditions") //passing null to Type<> argument
			@Override
			public <T extends BlockEntity> BlockEntityType<T> makeBlockEntityType(Factory<T> factory, Block... blocks) {
				return BlockEntityType.Builder.of(factory::create, blocks).build(null);
			}
		});
		
		//We have client entrypoint at home:
		if(FMLEnvironment.dist == Dist.CLIENT) {
			//Maximum classloading paranoia so you can't complain i didn't use distexecutor
			try {
				Class.forName("agency.highlysuspect.boatwitheverything.forge.ForgeClientNotEntrypoint").getConstructor().newInstance();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
