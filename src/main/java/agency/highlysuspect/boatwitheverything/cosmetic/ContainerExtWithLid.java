package agency.highlysuspect.boatwitheverything.cosmetic;

import net.minecraft.world.level.block.entity.LidBlockEntity;

public interface ContainerExtWithLid extends LidBlockEntity {
	void setShouldBeOpen(boolean shouldBeOpen);
}
