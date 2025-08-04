package im.f24.stickerbomb;

import im.f24.stickerbomb.client.render.StickerRenderer;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

@Environment(EnvType.CLIENT)
public class StickerBombClientMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register(StickerRenderer::renderStickers);

		ClientChunkEvents.CHUNK_LOAD.register(StickerWorldManager::onChunkLoaded);
		ClientChunkEvents.CHUNK_UNLOAD.register(StickerWorldManager::onChunkUnloaded);
	}
}
