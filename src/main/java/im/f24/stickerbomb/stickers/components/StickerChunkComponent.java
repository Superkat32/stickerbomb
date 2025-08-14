package im.f24.stickerbomb.stickers.components;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.stickers.StickerWorldInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.Random;

public class StickerChunkComponent implements AutoSyncedComponent, ServerTickingComponent {
	private static final ComponentKey<StickerChunkComponent> COMPONENT_KEY = StickerBombMod.STICKER_CHUNK_COMPONENT_COMPONENT_KEY;
	public static final Random random = new Random();

	public final Chunk chunk;
	public final ArrayList<StickerWorldInstance> stickers = new ArrayList<>();

	public int timer = 0;

	public StickerChunkComponent(Chunk c) {
		this.chunk = c;
		timer = random.nextInt(40);
	}


	/// Synced event, adds a sticker.
	/// Should only be called on the server!
	public void addSticker(StickerWorldInstance sticker) {
		stickers.add(sticker);
		COMPONENT_KEY.sync(this.chunk, (buf, recipient) -> {
			// Write single sticker add.
			buf.writeVarInt(1);
			StickerWorldInstance.PACKET_CODEC.encode(buf, sticker);
		});
		this.chunk.markNeedsSaving();
	}

	/// Synced event, removes a sticker.
	/// Should only be called on the server!
	public void removeSticker(StickerWorldInstance sticker) {
		// If no sticker was found, do nothing.
		if (!stickers.removeIf(s -> s.id.equals(sticker.id)))
			return;

		this.chunk.markNeedsSaving();
		COMPONENT_KEY.sync(this.chunk, (buf, rec) -> {
			// Write single sticker remove.
			buf.writeVarInt(2);
			buf.writeUuid(sticker.id);
		});
	}

	@Override
	public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
		// Write list of all stickers
		buf.writeVarInt(0);
		buf.writeCollection(this.stickers, StickerWorldInstance.PACKET_CODEC);
	}

	@Override
	public void applySyncPacket(RegistryByteBuf buf) {
		var mode = buf.readVarInt();
		switch (mode) {
			case 0: {
				// Sync entire sticker list.
				var collection = buf.readCollection(ArrayList::new, StickerWorldInstance.PACKET_CODEC);
				this.stickers.clear();
				this.stickers.addAll(collection);
				break;
			}
			case 1: {
				// Add single sticker
				var sticker = StickerWorldInstance.PACKET_CODEC.decode(buf);
				this.stickers.add(sticker);
				break;
			}
			case 2: {
				// Remove single sticker
				var id = buf.readUuid();
				this.stickers.removeIf(s -> s.id.equals(id));
				break;
			}
			case 3: {
				// Ignore.
				break;
			}

			default: {
				throw new RuntimeException("Unknown sync mode for sticker sync packet!!!");
			}
		}
	}

	@Override
	public void readData(ReadView readView) {
		this.stickers.clear();
		var opt = readView.read("stickers", StickerWorldInstance.CODEC.listOf());
		if (opt.isEmpty())
			return;
		this.stickers.addAll(opt.get());
	}

	@Override
	public void writeData(WriteView writeView) {
		if (this.stickers.isEmpty())
			return;
		writeView.put("stickers", StickerWorldInstance.CODEC.listOf(), this.stickers);
	}

	@Override
	public void serverTick() {
		timer++;
		if (timer <= 40) return;
		timer = 0;

		if (!(this.chunk instanceof WorldChunk worldChunk))
			return;

		var world = worldChunk.getWorld();

		// Check up to 100 random stickers for block attachment.
		for (int i = 0; i < Math.min(stickers.size(), 100); i++) {
			StickerWorldInstance sticker = stickers.get(random.nextInt(stickers.size()));

			if (StickerWorldInstance.isBlockPlacementValid(sticker.blockPos, sticker.side, world))
				continue;

			removeSticker(sticker);
			world.spawnEntity(sticker.createItemEntity(world));
		}
	}
}
