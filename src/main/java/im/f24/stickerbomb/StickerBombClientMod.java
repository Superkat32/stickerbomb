package im.f24.stickerbomb;

import com.google.common.collect.ImmutableList;
import im.f24.stickerbomb.client.render.StickerItemRenderer;
import im.f24.stickerbomb.client.render.StickerRenderer;
import im.f24.stickerbomb.client.screen.PrinterScreen;
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
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class StickerBombClientMod implements ClientModInitializer {
	public static ModelTransformation itemModelTransformation;

	public static ImmutableList<Identifier> FOUND_STICKERS = ImmutableList.of();

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register(StickerRenderer::renderStickers);

		ModelLoadingPlugin.register(new StickerItemRenderer.LoaderPlugin());

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

			@Override
			public void reload(ResourceManager manager) {
				var builder = ImmutableList.<Identifier>builder();

				var resources = manager.findResources("textures/sticker", (s) -> true);

				for (Identifier id : resources.keySet()) {
					var modifiedId = Identifier.of(id.getNamespace(), id.getPath().replace("textures/sticker/", "").replace(".png", ""));
					builder.add(modifiedId);
				}

				FOUND_STICKERS = builder.build();
			}

			@Override
			public Identifier getFabricId() {
				return Identifier.of(StickerBombMod.ID, "stickerbomb");
			}
		});


		HandledScreens.register(StickerBombMod.PRINTER_SCREEN_HANDLER, PrinterScreen::new);
	}
}
