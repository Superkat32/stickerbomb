package im.f24.stickerbomb.client.render;

import im.f24.stickerbomb.client.StickerAtlasHolder;
import im.f24.stickerbomb.stickers.StickerWorldInstance;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;

public class StickerRenderer {
	private static final ArrayList<StickerWorldInstance> STICKER_CACHE = new ArrayList<>();

	public static void renderStickers(WorldRenderContext worldRenderContext) {
		var worldRenderer = worldRenderContext.worldRenderer();
		var world = worldRenderContext.world();
		var camera = worldRenderContext.camera();
		var frustum = worldRenderContext.frustum();
		var builtChunks = worldRenderer.getBuiltChunks();

		STICKER_CACHE.clear();

		for (ChunkBuilder.BuiltChunk builtChunk : builtChunks) {
			StickerWorldManager.findStickers(world, ChunkSectionPos.from(builtChunk.getSectionPos()), STICKER_CACHE);
		}

		// Remove invisible stickers
		for (int i = STICKER_CACHE.size() - 1; i >= 0; i--) {
			var sticker = STICKER_CACHE.get(i);
			var localCameraPos = camera.getPos().subtract(sticker.position);
			var dir = sticker.side.getDoubleVector();
			var dot = dir.dotProduct(localCameraPos);

			if (dot < 0)
				STICKER_CACHE.remove(i);
			else if (!sticker.position.isInRange(camera.getPos(), 100))
				STICKER_CACHE.remove(i);
			else if (!frustum.isVisible(sticker.stickerBox))
				STICKER_CACHE.remove(i);
		}

		// Render stickers!

		var consumer = worldRenderContext.consumers().getBuffer(RenderLayer.getEntityCutout(StickerAtlasHolder.ID));
		var stack = worldRenderContext.matrixStack();

		for (StickerWorldInstance sticker : STICKER_CACHE) {
			stack.push();
			var side = sticker.side;

			stack.translate(sticker.position.subtract(camera.getPos()));
			stack.translate(0.5f, 0.5f, 0.5f);
			stack.multiply(side.getRotationQuaternion());

			var stickerSurfaceBlockPos = sticker.blockPos.offset(side);

			var matrix = stack.peek().getPositionMatrix();
			var light = LightmapTextureManager.pack(
				world.getLightLevel(LightType.BLOCK, stickerSurfaceBlockPos),
				world.getLightLevel(LightType.SKY, stickerSurfaceBlockPos)
			);

			var sprite = StickerAtlasHolder.INSTANCE.getSticker(sticker.stickerTextureId);
			var spriteConsumer = sprite.getTextureSpecificVertexConsumer(consumer);

			vertex(spriteConsumer, matrix, -0.5f, 0.501f, 0.5f, 0.0F, 1.0F, light);
			vertex(spriteConsumer, matrix, 0.5f, 0.501f, 0.5f, 1.0F, 1.0F, light);
			vertex(spriteConsumer, matrix, 0.5f, 0.501f, -0.5f, 1.0F, 0.0F, light);
			vertex(spriteConsumer, matrix, -0.5f, 0.501f, -0.5f, 0.0F, 0.0F, light);
			
			stack.pop();
		}
	}

	private static void vertex(VertexConsumer consumer, Matrix4f positionMatrix, float x, float y, float z, float u, float v, int light) {
		consumer.vertex(positionMatrix, x, y, z);
		consumer.color(Colors.WHITE);
		consumer.texture(u, v);
		consumer.normal(0.0F, 1.0F, 0.0F);
		consumer.light(light);
		consumer.overlay(OverlayTexture.DEFAULT_UV);
	}
}
