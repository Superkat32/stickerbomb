package im.f24.stickerbomb;

import im.f24.stickerbomb.entity.StickerEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StickerBombMod implements ModInitializer {
	public static final String ID = "stickerbomb";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final EntityType<StickerEntity> STICKER_ENTITY = Registry.register(
		Registries.ENTITY_TYPE,
		Identifier.of(ID, "sticker"),
		EntityType.Builder.create(StickerEntity::new, SpawnGroup.MISC)
			.dimensions(0.5f, 0.5f)
			.eyeHeight(0.0f)
			.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ID, "sticker")))
	);

	@Override
	public void onInitialize() {
		//LOGGER.info("[Mod ID] pretty pink princess ponies prancing perpendicular");
	}
}
