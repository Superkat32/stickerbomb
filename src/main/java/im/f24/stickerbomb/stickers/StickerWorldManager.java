package im.f24.stickerbomb.stickers;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.stickers.components.StickerChunkComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StickerWorldManager {
	private static final ComponentKey<StickerChunkComponent> COMPONENT_KEY = StickerBombMod.STICKER_CHUNK_COMPONENT_COMPONENT_KEY;
	private static final List<StickerWorldInstance> STICKER_CACHE = new ArrayList<>();

	// Sticker Management //

	private static StickerChunkComponent getComponent(World world, ChunkPos pos) {
		if (!world.isChunkLoaded(pos.x, pos.z))
			return null;

		var chunk = world.getChunk(pos.x, pos.z);
		return COMPONENT_KEY.get(chunk);
	}

	/// Adds a sticker to a world.
	public static void addSticker(ServerWorld world, StickerWorldInstance sticker) {
		if (!(getComponent(world, sticker.getChunkPos()) instanceof StickerChunkComponent component))
			return;

		component.addSticker(sticker);
	}

	/// Removes a specific sticker from a world.
	/// If the sticker is not present, this does nothing.
	public static void removeSticker(ServerWorld world, StickerWorldInstance sticker) {
		if (!(getComponent(world, sticker.getChunkPos()) instanceof StickerChunkComponent component))
			return;

		component.removeSticker(sticker);
	}

	public static void removeStickers(ServerWorld world, BlockPos pos, Direction side) {
		findStickers(world, new ChunkPos(pos), STICKER_CACHE);

		for (StickerWorldInstance sticker : STICKER_CACHE)
			if (sticker.blockPos.equals(pos) && sticker.side.equals(side))
				removeSticker(world, sticker);
	}

	/// Finds all the stickers within a given chunk and populates a list with them.
	public static void findStickers(World world, ChunkPos chunkPos, List<StickerWorldInstance> targetList) {
		if (!(getComponent(world, chunkPos) instanceof StickerChunkComponent component))
			return;

		targetList.addAll(component.stickers);
	}

	/// Finds all the stickers within a given chunk and populates a list with them.
	public static void findStickers(World world, ChunkSectionPos sectionPos, List<StickerWorldInstance> targetList) {
		if (!(getComponent(world, sectionPos.toChunkPos()) instanceof StickerChunkComponent component))
			return;

		for (StickerWorldInstance sticker : component.stickers)
			if (sticker.position.y >= sectionPos.getMinY() && sticker.position.y <= sectionPos.getMaxY())
				targetList.add(sticker);
	}
}
