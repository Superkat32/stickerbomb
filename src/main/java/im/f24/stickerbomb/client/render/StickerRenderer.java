package im.f24.stickerbomb.client.render;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.StickerPlacementUtils;
import im.f24.stickerbomb.client.StickerAtlasHolder;
import im.f24.stickerbomb.items.StickerItem;
import im.f24.stickerbomb.stickers.StickerWorldInstance;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
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
		var stack = worldRenderContext.matrixStack();

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

		var consumer = worldRenderContext.consumers().getBuffer(RenderLayer.getEntityCutout(StickerAtlasHolder.ID));


		// Render sticker preview
		{
			var client = MinecraftClient.getInstance();
			var player = client.player;
			var item = player.getStackInHand(player.getActiveHand());

			if (item.getItem() instanceof StickerItem && client.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS) {
				var pos = blockHitResult.getPos().subtract(blockHitResult.getSide().getDoubleVector().multiply(0.499f));
				var lerpedPos = player.getLerpedPos(client.getRenderTickCounter().getTickProgress(true));
				var rotation = StickerPlacementUtils.getRotationForPlacement(blockHitResult.getPos(), lerpedPos.withAxis(Direction.Axis.Y, player.getEyeY()), blockHitResult.getSide());

				stack.push();

				var stickerID = item.get(StickerBombMod.STICKER_ID);
				var sprite = StickerAtlasHolder.INSTANCE.getSticker(stickerID);
				var spriteConsumer = sprite.getTextureSpecificVertexConsumer(consumer);

				setupStickerRenderState(pos.subtract(camera.getPos()), blockHitResult.getSide(), stack, rotation);
				renderStickerQuad(spriteConsumer, stack, LightmapTextureManager.MAX_LIGHT_COORDINATE);
				stack.pop();
			}
		}

		// Render stickers!

		{
			for (StickerWorldInstance sticker : STICKER_CACHE) {
				renderSticker(sticker, camera, world, stack, consumer);
			}
		}
	}

	public static void renderSticker(StickerWorldInstance sticker, Camera camera, World world, MatrixStack stack, VertexConsumer consumer) {
		stack.push();

		setupStickerRenderState(sticker.position.subtract(camera.getPos()), sticker.side, stack, sticker.rotation);

		var stickerSurfaceBlockPos = sticker.blockPos.offset(sticker.side);
		var light = LightmapTextureManager.pack(
			world.getLightLevel(LightType.BLOCK, stickerSurfaceBlockPos),
			world.getLightLevel(LightType.SKY, stickerSurfaceBlockPos)
		);

		var sprite = StickerAtlasHolder.INSTANCE.getSticker(sticker.stickerTextureId);
		var spriteConsumer = sprite.getTextureSpecificVertexConsumer(consumer);

		renderStickerQuad(spriteConsumer, stack, light);

		stack.pop();
	}

	public static void setupStickerRenderState(Vec3d position, Direction side, MatrixStack stack, float rotation) {
		stack.translate(position);
		stack.multiply(new Quaternionf().rotationAxis(rotation, side.getFloatVector()));
		stack.multiply(side.getRotationQuaternion());
		stack.multiply(new Quaternionf().rotationX((float) Math.PI * 0.5f));
		stack.translate(-0.5f, -0.5f, -0.501f);
	}

	public static void renderStickerQuad(VertexConsumer consumer, MatrixStack stack, int light) {
		vertex(consumer, stack, 0, 1, 0, 0.0F, 1.0F, light);
		vertex(consumer, stack, 1, 1, 0, 1.0F, 1.0F, light);
		vertex(consumer, stack, 1, 0, 0, 1.0F, 0.0F, light);
		vertex(consumer, stack, 0, 0, 0, 0.0F, 0.0F, light);
	}

	private static void vertex(VertexConsumer consumer, MatrixStack stack, float x, float y, float z, float u, float v, int light) {
		consumer.vertex(stack.peek().getPositionMatrix(), x, y, z);
		consumer.color(Colors.WHITE);
		consumer.texture(u, v);
		consumer.normal(stack.peek(), 0, 0, -1);
		consumer.light(light);
		consumer.overlay(OverlayTexture.DEFAULT_UV);
	}
}
