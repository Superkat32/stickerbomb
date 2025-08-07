package im.f24.stickerbomb.stickers.components;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.stickers.StickerWorldInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.chunk.Chunk;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;

public class StickerChunkComponent implements AutoSyncedComponent, ServerTickingComponent {
	private static final ComponentKey<StickerChunkComponent> COMPONENT_KEY = StickerBombMod.STICKER_CHUNK_COMPONENT_COMPONENT_KEY;

	public final Chunk chunk;
	public final ArrayList<StickerWorldInstance> stickers = new ArrayList<>();

	public StickerChunkComponent(Chunk c) {
		this.chunk = c;
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
	}

	/// Synced event, removes a sticker.
	/// Should only be called on the server!
	public void removeSticker(StickerWorldInstance sticker) {
		// If no sticker was found, do nothing.
		if (!stickers.removeIf(s -> s.id.equals(sticker.id)))
			return;

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
		switch (buf.readVarInt()) {
			case 0: {
				// Sync entire sticker list.
				var collection = buf.readCollection(ArrayList::new, StickerWorldInstance.PACKET_CODEC);
				this.stickers.clear();
				this.stickers.addAll(collection);
			}
			case 1: {
				// Add single sticker
				var sticker = StickerWorldInstance.PACKET_CODEC.decode(buf);
				this.stickers.add(sticker);
			}
			case 2: {
				// Remove single sticker
				var id = buf.readUuid();
				this.stickers.removeIf(s -> s.id.equals(id));
			}

			default: {
				throw new RuntimeException("Unknown sync mode for sticker sync packet!!!");
			}
		}
	}

	@Override
	public void readData(ReadView readView) {

	}

	@Override
	public void writeData(WriteView writeView) {

	}

	@Override
	public void serverTick() {

	}
}
