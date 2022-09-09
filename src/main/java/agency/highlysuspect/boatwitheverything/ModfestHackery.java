package agency.highlysuspect.boatwitheverything;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ModfestHackery {
	public static final EntityDataSerializer<Optional<Vec3>> OPTIONAL_VEC3_SERIALIZER = new EntityDataSerializer.ForValueType<>() {
		@Override
		public void write(FriendlyByteBuf buf, Optional<Vec3> vec) {
			if(vec.isPresent()) {
				buf.writeBoolean(true);
				buf.writeDouble(vec.get().x);
				buf.writeDouble(vec.get().y);
				buf.writeDouble(vec.get().z);
			} else buf.writeBoolean(false);
		}
		
		@Override
		public Optional<Vec3> read(FriendlyByteBuf buf) {
			if(buf.readBoolean())	return Optional.of(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
			else return Optional.empty();
		}
	};
}
