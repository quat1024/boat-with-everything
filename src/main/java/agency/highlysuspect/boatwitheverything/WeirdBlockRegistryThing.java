package agency.highlysuspect.boatwitheverything;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WeirdBlockRegistryThing<T> {
	//default value
	public WeirdBlockRegistryThing(@NotNull T def) {
		this.def = def;
	}
	
	private final Map<Block, T> blocks = new HashMap<>();
	private final Map<TagKey<Block>, T> tags = new LinkedHashMap<>();
	private final Map<Class<?>, T> classes = new LinkedHashMap<>();
	private final @NotNull T def;
	
	public void put(T thing, Block... blocks) {
		for(Block b : blocks) this.blocks.put(b, thing);
	}
	
	@SafeVarargs
	public final void put(T thing, TagKey<Block>... tags) {
		for(TagKey<Block> tag : tags) this.tags.put(tag, thing);
	}
	
	public void put(T thing, Class<?>... classes) {
		for(Class<?> classs : classes) this.classes.put(classs, thing);
	}
	
	@SuppressWarnings("unchecked")
	public final void putMixed(T thing, Object... keys) {
		for(Object what : keys) {
			if(what instanceof Block b) put(thing, b);
			else if(what instanceof TagKey<?> tag) put(thing, (TagKey<Block>) tag);
			else if(what instanceof Class<?> classs) put(thing, classs);
			else throw new IllegalArgumentException("Dunno what to do with" + what.getClass());
		}
	}
	
	public @NotNull T get(BlockState state) {
		//blocks
		Block block = state.getBlock();
		T b = blocks.get(block);
		if(b != null) return b;
		
		//block tags
		for(TagKey<Block> tag : tags.keySet()) {
			if(state.is(tag)) return tags.get(tag);
		}
		
		//instanceof
		for(Class<?> classs : classes.keySet()) {
			if(classs.isInstance(block)) return classes.get(classs);
		}
		
		//shrug emoji
		return def;
	}
}
