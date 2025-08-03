package im.f24.stickerbomb;

import im.f24.stickerbomb.client.StickerAtlasHolder;
import im.f24.stickerbomb.client.StickerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class StickerBombClientMod implements ClientModInitializer {

	public static final Identifier STICKER_ATLAS_TEXTURE = Identifier.of(StickerBombMod.ID, "textures/atlas/sticker.png");
	public static final RenderLayer STICKER_RENDER_LAYER = RenderLayer.getEntitySolid(STICKER_ATLAS_TEXTURE);
	public static final SpriteMapper STICKER_SPRITE_MAPPER = new SpriteMapper(STICKER_ATLAS_TEXTURE, "sticker");

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(StickerBombMod.STICKER_ENTITY, StickerRenderer::new);


	}


}
