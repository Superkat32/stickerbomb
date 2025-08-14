package im.f24.stickerbomb.items;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.StickerPlacementUtils;
import im.f24.stickerbomb.stickers.StickerWorldInstance;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class StickerItem extends Item {
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

		var id = context.getStack().getOrDefault(StickerBombMod.STICKER_ID, Identifier.of(StickerBombMod.ID, "test"));
		var stickerInstance = new StickerWorldInstance(id);
		stickerInstance.setPosition(context.getHitPos().subtract(context.getSide().getDoubleVector().multiply(0.5f)));
		stickerInstance.side = context.getSide();
		stickerInstance.rotation = StickerPlacementUtils.getRotationForPlacement(stickerInstance.position, context.getPlayer().getEyePos(), context.getSide());
		StickerWorldManager.addSticker(world, stickerInstance);

		context.getStack().decrement(1);
		return ActionResult.SUCCESS;
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		var id = stack.get(StickerBombMod.STICKER_ID);
		textConsumer.accept(Text.translatable("item.stickerbomb.sticker.id", id).formatted(Formatting.DARK_GRAY));
	}

	public static ItemStack stackWithId(Identifier id) {
		var stack = new ItemStack(StickerBombMod.STICKER_ITEM);
		stack.set(StickerBombMod.STICKER_ID, id);
		return stack;
	}
}
