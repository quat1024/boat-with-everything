package agency.highlysuspect.boatwitheverything;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class WeirdBlockRegistryThing<T> {
	//default value
	public WeirdBlockRegistryThing(@NotNull T def) {
		this.def = def;
	}
	
	private final Map<Block, T> blocks = new HashMap<>();
	private final Map<TagKey<Block>, T> tags = new LinkedHashMap<>();
	private final Map<Class<?>, T> classes = new LinkedHashMap<>();
	private final Map<Predicate<BlockState>, T> oddballs = new LinkedHashMap<>();
	private final @NotNull T def;
	
	public void putBlock(T thing, Block... blocks) {
		for(Block b : blocks) this.blocks.put(b, thing);
	}
	
	@SafeVarargs
	public final void putBlockTag(T thing, TagKey<Block>... tags) {
		for(TagKey<Block> tag : tags) this.tags.put(tag, thing);
	}
	
	public void putClass(T thing, Class<?>... classes) {
		for(Class<?> classs : classes) this.classes.put(classs, thing);
	}
	
	@SafeVarargs
	public final void putSpecial(T thing, Predicate<BlockState>... oddballs) {
		for(Predicate<BlockState> oddball : oddballs) this.oddballs.put(oddball, thing);
	}
	
	@SuppressWarnings("unchecked")
	public final void putMixed(T thing, Object... keys) {
		for(Object what : keys) {
			if(what instanceof Block b) putBlock(thing, b);
			else if(what instanceof TagKey<?> tag) putBlockTag(thing, (TagKey<Block>) tag);
			else if(what instanceof Class<?> classs) putClass(thing, classs);
			else if(what instanceof Predicate<?> o) putSpecial(thing, (Predicate<BlockState>) o);
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
		
		//odds and ends
		for(Predicate<BlockState> oddball : oddballs.keySet()) {
			if(oddball.test(state)) return oddballs.get(oddball);
		}
		
		//shrug emoji
		return def;
	}
}
