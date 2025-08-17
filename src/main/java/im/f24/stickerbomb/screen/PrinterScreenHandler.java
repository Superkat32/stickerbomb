package im.f24.stickerbomb.screen;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.blocks.StickerPrinterBlock;
import im.f24.stickerbomb.client.StickerAtlasHolder;
import im.f24.stickerbomb.items.StickerItem;
import im.f24.stickerbomb.network.PrinterScreenProvideIDC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class PrinterScreenHandler extends ScreenHandler {

	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	private final CraftingInventory craftingInventory;
	private final CraftingResultInventory craftingResultInventory = new CraftingResultInventory();

	private Optional<Identifier> current_id = Optional.empty();

	public PrinterScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	public PrinterScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(StickerBombMod.PRINTER_SCREEN_HANDLER, syncId);

		this.context = context;
		this.player = inventory.player;

		this.craftingInventory = new CraftingInventory(this, 1, 2);
		this.addSlot(new ResultSlot(craftingResultInventory, 0, 124, 35));
		this.addSlot(new Slot(this.craftingInventory, 0, 68, 17));
		this.addSlot(new Slot(this.craftingInventory, 1, 68, 53));
		this.addPlayerSlots(inventory, 8, 84);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		super.onContentChanged(inventory);

		var resultSlot = getSlot(0);

		var topSlot = getSlot(1);
		var bottomSlot = getSlot(2);

		resultSlot.setStack(ItemStack.EMPTY);

		// Ensure we have *some* ID set.
		if (current_id.isEmpty())
			return;

		// Check bottom slot for blank stickers
		if (!bottomSlot.hasStack() || bottomSlot.getStack().getItem() != StickerBombMod.BLANK_STICKER_ITEM)
			return;

		// Check top slot for dyes
		if (!topSlot.hasStack())
			return;
		if (topSlot.getStack().streamTags().noneMatch(t -> t.equals(ConventionalItemTags.DYES)))
			return;

		var stack = new ItemStack(StickerBombMod.STICKER_ITEM, 1);
		var isTemp = false;

		if (context.get(PrinterScreenHandler::isBlockTempset).orElse(false))
			isTemp = true;

		stack.set(StickerItem.STICKER_DATA_COMPONENT, new StickerItem.StickerItemData(current_id.get(), false, isTemp));

		craftingResultInventory.setStack(0, stack);
		setReceivedStack(0, stack);

		if (player instanceof ServerPlayerEntity serverPlayer)
			serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 0, stack));
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.craftingInventory));
	}

	public void setStickerId(String content) {
		if (content.isEmpty()) {
			this.current_id = Optional.empty();
			return;
		}

		if(!content.contains(":"))
			content = "stickerbomb:" + content;

		this.current_id = Optional.ofNullable(Identifier.tryParse(content));

		// Check if valid ID is provided.
		if (player.getWorld().isClient() && current_id.isPresent()) {
			if (!StickerAtlasHolder.INSTANCE.hasSticker(current_id.get())) {
				this.current_id = Optional.empty();
			}

			// Tell server we decided on this ID.
			ClientPlayNetworking.send(new PrinterScreenProvideIDC2SPayload(this.current_id));
		}

		onContentChanged(this.craftingInventory);
	}

	public void setStickerId(Optional<Identifier> id) {
		this.current_id = id;
		onContentChanged(this.craftingInventory);
	}

	private static boolean isBlockTempset(World world, BlockPos pos) {
		var blockState = world.getBlockState(pos);

		return blockState.get(StickerPrinterBlock.TEMPORARY, false);
	}

	public class ResultSlot extends Slot {

		public ResultSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			super.onTakeItem(player, stack);

			var topSlot = getSlot(1);
			var bottomSlot = getSlot(2);

			topSlot.takeStack(1);
			bottomSlot.takeStack(1);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return false;
		}
	}
}
