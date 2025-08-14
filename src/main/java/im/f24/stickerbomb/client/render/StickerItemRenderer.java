package im.f24.stickerbomb.client.render;

import com.mojang.serialization.MapCodec;
import im.f24.stickerbomb.StickerBombClientMod;
import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.client.StickerAtlasHolder;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class StickerItemRenderer implements SpecialModelRenderer<ComponentMap> {

	@Override
	public void render(
		@Nullable ComponentMap data,
		ItemDisplayContext displayContext,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		boolean glint
	) {
		/*var stickerId = Identifier.of(StickerBombMod.ID, "invalid");
		if (data != null)
			stickerId = data.getOrDefault(StickerBombMod.STICKER_ID, stickerId);

		var rootConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(StickerAtlasHolder.ID));
		var sprite = StickerAtlasHolder.INSTANCE.getSticker(stickerId);
		var spriteConsumer = sprite.getTextureSpecificVertexConsumer(rootConsumer);

		var transform = StickerBombClientMod.itemModelTransformation.getTransformation(displayContext);

		/*matrices.push();
		transform.apply(displayContext.isLeftHand(), matrices.peek());
		matrices.translate(1, 2, 1);
		matrices.multiply(Direction.DOWN.getRotationQuaternion());
		//matrices.scale(1, -1, -1);
		//StickerRenderer.renderSticker(spriteConsumer, matrices, light);
		matrices.pop();*/
	}

	@Override
	public void collectVertices(Set<Vector3f> vertices) {

	}

	@Override
	public @Nullable ComponentMap getData(ItemStack stack) {
		return stack.getImmutableComponents();
	}

	public static class LoaderPlugin implements ModelLoadingPlugin {

		@Override
		public void initialize(Context pluginContext) {
			pluginContext.modifyItemModelBeforeBake().register((model, context) -> {
				/*if (StickerBombMod.STICKER_ITEM_ID.equals(context.itemId()))
					return ItemModels.special(context.itemId(), new Unbaked());
				else*/
					return model;
			});
		}
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked {
		public static final StickerItemRenderer.Unbaked INSTANCE = new StickerItemRenderer.Unbaked();
		public static final MapCodec<StickerItemRenderer.Unbaked> CODEC = MapCodec.unit(INSTANCE);

		@Override
		public MapCodec<StickerItemRenderer.Unbaked> getCodec() {
			return CODEC;
		}

		@Override
		public @Nullable SpecialModelRenderer<ComponentMap> bake(LoadedEntityModels entityModels) {
			return new StickerItemRenderer();
		}
	}
}
