package im.f24.stickerbomb.items;

import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class StickerScraper extends Item {
	public StickerScraper(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		var world = context.getWorld();
		if(world.isClient)
			return ActionResult.SUCCESS;

		StickerWorldManager.removeStickers((ServerWorld) world, context.getBlockPos(), context.getSide());
		return super.useOnBlock(context);
	}
}
