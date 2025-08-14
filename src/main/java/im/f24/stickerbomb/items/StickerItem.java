package im.f24.stickerbomb.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.StickerPlacementUtils;
import im.f24.stickerbomb.stickers.StickerWorldInstance;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class StickerItem extends Item {
	public static final ComponentType<StickerItemData> STICKER_DATA_COMPONENT = Registry.register(
		Registries.DATA_COMPONENT_TYPE,
		Identifier.of(StickerBombMod.ID, "sticker_data"),
		ComponentType.<StickerItemData>builder().codec(StickerItemData.CODEC).build()
	);

	public StickerItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getWorld().isClient)
			return ActionResult.SUCCESS;

		var world = (ServerWorld) context.getWorld();

		if (!StickerWorldInstance.isBlockPlacementValid(context.getBlockPos(), context.getSide(), world))
			return ActionResult.FAIL;

		var data = context.getStack().getOrDefault(STICKER_DATA_COMPONENT, StickerItemData.DEFAULT);
		var stickerInstance = new StickerWorldInstance(data.id);
		stickerInstance.setPosition(context.getHitPos().subtract(context.getSide().getDoubleVector().multiply(0.5f)));
		stickerInstance.side = context.getSide();
		stickerInstance.rotation = StickerPlacementUtils.getRotationForPlacement(stickerInstance.position, context.getPlayer().getEyePos(), context.getSide());
		stickerInstance.isAdminSticker = data.isAdmin;
		stickerInstance.creator = context.getPlayer().getUuid();
		stickerInstance.temporaryTimer = data.isTemporary ? 600 : -1;
		StickerWorldManager.addSticker(world, stickerInstance);

		context.getStack().decrement(1);
		return ActionResult.SUCCESS;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		var data = stack.getOrDefault(STICKER_DATA_COMPONENT, StickerItemData.DEFAULT);
		textConsumer.accept(Text.translatable("item.stickerbomb.sticker.id", data.id).formatted(Formatting.DARK_GRAY));

		if (data.isTemporary)
			textConsumer.accept(Text.translatable("item.stickerbomb.sticker.temporary").formatted(Formatting.BOLD, Formatting.ITALIC, Formatting.DARK_GRAY));
		if (data.isAdmin)
			textConsumer.accept(Text.translatable("item.stickerbomb.sticker.admin").formatted(Formatting.BOLD, Formatting.ITALIC, Formatting.GOLD));
	}

	public static ItemStack stackWithData(StickerItemData data) {
		var stack = new ItemStack(StickerBombMod.STICKER_ITEM);
		stack.set(STICKER_DATA_COMPONENT, data);
		return stack;
	}

	public record StickerItemData(Identifier id, boolean isAdmin, boolean isTemporary) {
		public static Codec<StickerItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("id").forGetter(StickerItemData::id),
			Codec.BOOL.optionalFieldOf("isAdmin", false).forGetter(StickerItemData::isAdmin),
			Codec.BOOL.optionalFieldOf("isTemporary", false).forGetter(StickerItemData::isTemporary)
		).apply(instance, StickerItemData::new));

		public static final StickerItemData DEFAULT = new StickerItemData(Identifier.of(StickerBombMod.ID, "test"), false, false);
	}
}
