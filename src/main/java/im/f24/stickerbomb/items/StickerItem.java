package im.f24.stickerbomb.items;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.stickers.StickerWorldInstance;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class StickerItem extends Item {
	public StickerItem(Settings settings) {
		super(settings);
	}


	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getWorld().isClient)
			return ActionResult.SUCCESS;

		var world = (ServerWorld) context.getWorld();

		var id = context.getStack().getOrDefault(StickerBombMod.STICKER_ID, Identifier.of(StickerBombMod.ID, "test"));
		var stickerInstance = new StickerWorldInstance(id);
		stickerInstance.setPosition(context.getBlockPos().toCenterPos());
		stickerInstance.side = context.getSide();
		StickerWorldManager.addSticker(world, stickerInstance);

		context.getStack().decrement(1);
		return ActionResult.SUCCESS;
	}
}
