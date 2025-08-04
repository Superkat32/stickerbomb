package im.f24.stickerbomb.stickers;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StickerWorldManager {
	private static final HashMap<Chunk, List<StickerWorldInstance>> STICKER_DATA = new HashMap<>();

	// Sticker Management //

	/// Adds a sticker to a world.
	public static void addSticker(World world, StickerWorldInstance stickerWorldInstance) {
		var stickerList = getStickerList(world, stickerWorldInstance.getChunkPos());
		if (stickerList == null)
			return;

		stickerList.add(stickerWorldInstance);
	}

	/// Removes a specific sticker from a world.
	/// If the sticker is not present, this does nothing.
	public static void removeSticker(World world, StickerWorldInstance stickerWorldInstance) {
		var stickerList = getStickerList(world, stickerWorldInstance.getChunkPos());
		if (stickerList == null)
			return;

		stickerList.remove(stickerWorldInstance);
	}

	/// Finds all the stickers within a given chunk and populates a list with them.
	public static void findStickers(World world, ChunkPos chunkPos, List<StickerWorldInstance> targetList) {
		var stickerList = getStickerList(world, chunkPos);
		if (stickerList == null)
			return;

		targetList.addAll(stickerList);
	}

	/// Finds all the stickers within a given chunk and populates a list with them.
	public static void findStickers(World world, ChunkSectionPos sectionPos, List<StickerWorldInstance> targetList) {
		var stickerList = getStickerList(world, sectionPos.toChunkPos());
		if (stickerList == null)
			return;

		for (StickerWorldInstance sticker : stickerList) {
			var minY = sectionPos.getMinY();
			var maxY = sectionPos.getMaxY();
			if (minY > sticker.position.y || maxY < sticker.position.y)
				continue;
			targetList.add(sticker);
		}
	}

	/// Gets the sticker list for a loaded chunk.
	/// If the given chunk is not loaded, returns null.
	private static List<StickerWorldInstance> getStickerList(World world, ChunkPos pos) {
		if (!world.isChunkLoaded(pos.x, pos.z))
			return null;
		return STICKER_DATA.get(world.getChunk(pos.x, pos.z));
	}

	// Events //
	private static final List<StickerWorldInstance> TICK_STICKER_LIST = new ArrayList<>();
	private static int stickerCheckTimer = 0;

	public static void onChunkLoaded(World world, Chunk chunk) {
		var stickerList = new ArrayList<StickerWorldInstance>();
		STICKER_DATA.put(chunk, stickerList);

		var sticker = new StickerWorldInstance(Identifier.of("stickerbomb:test"));
		stickerList.add(sticker);

		sticker.setPosition(new Vec3d(
			chunk.getPos().getCenterX(),
			150,
			chunk.getPos().getCenterZ()
		));
	}

	public static void onChunkUnloaded(World world, Chunk chunk) {
		STICKER_DATA.remove(chunk);
	}

	/// Should be called whenever the world ticks.
	/// Will be used to 'pop off' any stickers that are incorrectly attached to blocks.
	public static void onWorldTick(ServerWorld world) {

		// Only execute the check every 100 ticks
		stickerCheckTimer++;
		if (stickerCheckTimer < 100)
			return;
		stickerCheckTimer = 0;

		TICK_STICKER_LIST.clear();
		world.getChunkManager().chunkLoadingManager.getLevelManager().forEachBlockTickingChunk(chunkPos -> {
			findStickers(world, new ChunkPos(chunkPos), TICK_STICKER_LIST);
		});

		for (StickerWorldInstance sticker : TICK_STICKER_LIST) {
			var blockPos = sticker.blockPos;
			var state = world.getBlockState(blockPos);

			//If the face the sticker is attached to is a full block, skip.
			if (state.isSideSolidFullSquare(world, blockPos, sticker.side))
				continue;

			// If not, pop off (spawn item and such)
			var itemEntity = new ItemEntity(
				world,
				sticker.position.x, sticker.position.y, sticker.position.z,
				new ItemStack(Items.ITEM_FRAME) //TODO - Put item here!
			);
			world.spawnEntity(itemEntity);

			removeSticker(world, sticker);
		}
	}
}
