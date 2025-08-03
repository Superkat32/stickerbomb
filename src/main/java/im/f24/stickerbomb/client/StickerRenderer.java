package im.f24.stickerbomb.client;

import im.f24.stickerbomb.StickerBombClientMod;
import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.entity.StickerContainerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class StickerRenderer extends EntityRenderer<StickerContainerEntity, StickerEntityRenderState> {
	public static final SpriteMapper SPRITE_MAPPER = new SpriteMapper(StickerBombClientMod.STICKER_ATLAS_TEXTURE, "sticker");

	//TODO: using this is pain, see net.minecraft.client.render.model.BakedModelManager.LAYERS_TO_LOADERS as a jumping off point
//	public static final SpriteIdentifier TEST_TEXTURE = SPRITE_MAPPER.map(Identifier.of(StickerBombMod.ID, "test"));

	public StickerRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public StickerEntityRenderState createRenderState() {
		return new StickerEntityRenderState();
	}

	@Override
	public void updateRenderState(StickerContainerEntity entity, StickerEntityRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);
	}

	@Override
	public void render(StickerEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();

		var posMatrix = matrices.peek().getPositionMatrix();
		var sprite = StickerAtlasHolder.INSTANCE.getSticker(Identifier.of(StickerBombMod.ID, "test"));
		var consumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(RenderLayer.getText(sprite.getAtlasId())));

		vertex(consumer, posMatrix, -0.5F, 0.5F, 0.5f - 0.01f, 1.0F, 0.0F, light);
		vertex(consumer, posMatrix, 0.5F, 0.5F, 0.5f - 0.01f, 0.0F, 0.0F, light);
		vertex(consumer, posMatrix, 0.5F, -0.5F, 0.5f - 0.01f, 0.0F, 1.0F, light);
		vertex(consumer, posMatrix, -0.5F, -0.5F, 0.5f - 0.01f, 1.0F, 1.0F, light);

		matrices.pop();
	}

	private void vertex(VertexConsumer consumer, Matrix4f positionMatrix, float x, float y, float z, float u, float v, int light) {
		//todo: pool vector
		//note: chaining breaks wrapped consumer
		consumer.vertex(positionMatrix, x, y, z);
		consumer.color(Colors.WHITE);
		consumer.texture(u, v);
		consumer.light(light);
	}
}
