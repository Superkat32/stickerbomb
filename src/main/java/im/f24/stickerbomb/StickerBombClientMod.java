package im.f24.stickerbomb;

import im.f24.stickerbomb.client.render.StickerItemRenderer;
import im.f24.stickerbomb.client.render.StickerRenderer;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;

@Environment(EnvType.CLIENT)
public class StickerBombClientMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register(StickerRenderer::renderStickers);

		ModelLoadingPlugin.register(new StickerItemRenderer.LoaderPlugin());
	}
}
