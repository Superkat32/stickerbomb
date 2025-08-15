package im.f24.stickerbomb.client;

import im.f24.stickerbomb.StickerBombMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class StickerAtlasHolder extends SpriteAtlasHolder {
	public static final Identifier ID = Identifier.of(StickerBombMod.ID, "textures/atlas/stickers.png");

	@NotNull
	public static StickerAtlasHolder INSTANCE = null;

	public StickerAtlasHolder(TextureManager textureManager) {
		super(
			textureManager,
			ID,
			//source path to load atlas file from
			Identifier.of(StickerBombMod.ID, "stickers")
		);
	}

	public Sprite getSticker(Identifier id) {
		return getSprite(id);
	}

	public boolean hasSticker(Identifier id) {
		var result = getSticker(id);
		var missingSprite = getSticker(Identifier.of(""));
		var isMissing = result == missingSprite;
		return !isMissing;
	}

	public float getScaleForStickerSprite(Sprite sprite) {
		var max = Math.max(sprite.getContents().getWidth(), sprite.getContents().getHeight());
		return (float) max / StickerBombMod.STICKER_PIXELS_PER_BLOCK;
	}
}
