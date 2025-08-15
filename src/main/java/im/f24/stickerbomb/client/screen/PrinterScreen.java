package im.f24.stickerbomb.client.screen;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.screen.PrinterScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PrinterScreen extends HandledScreen<PrinterScreenHandler> {
	private static final Identifier TEXTURE = Identifier.of(StickerBombMod.ID, "textures/gui/printer_screen.png");

	public TextFieldWidget idBar;

	public PrinterScreen(PrinterScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();

		idBar = new TextFieldWidget(this.textRenderer, x, y, 81, 14, Text.translatable("screen.stickerbomb.printer.id"));
		idBar.setChangedListener(handler::setStickerId);
		addDrawableChild(idBar);
	}

	@Override
	protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
		context.drawTexture(
			RenderPipelines.GUI_TEXTURED, TEXTURE,
			this.x, (this.height - this.backgroundHeight) / 2,
			0.0F, 0.0F,
			this.backgroundWidth, this.backgroundHeight, 256, 256
		);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);

		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (idBar.isFocused() && this.client.options.inventoryKey.matchesKey(keyCode, scanCode))
			return idBar.keyPressed(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
