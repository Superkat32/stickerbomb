package im.f24.stickerbomb.stickers.components;

import im.f24.stickerbomb.StickerBombMod;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;

public class StickerChunkComponentInitializer implements ChunkComponentInitializer {
	@Override
	public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
		registry.register(StickerBombMod.STICKER_CHUNK_COMPONENT_COMPONENT_KEY, StickerChunkComponent::new);
	}
}
