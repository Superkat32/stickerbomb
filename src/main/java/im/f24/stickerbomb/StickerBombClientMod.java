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
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class StickerBombClientMod implements ClientModInitializer {
	public static ModelTransformation itemModelTransformation;

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register(StickerRenderer::renderStickers);

		ModelLoadingPlugin.register(new StickerItemRenderer.LoaderPlugin());

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

			@Override
			public void reload(ResourceManager manager) {
				var res = manager.getResource(Identifier.of("minecraft", "models/item/generated.json"));

				if (res.isEmpty())
					throw new RuntimeException("How did you even end up here???");

				try {
					var mdl = JsonUnbakedModel.deserialize(res.get().getReader());
					itemModelTransformation = mdl.transformations();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public Identifier getFabricId() {
				return Identifier.of(StickerBombMod.ID, "stickerbomb");
			}
		});
	}
}
