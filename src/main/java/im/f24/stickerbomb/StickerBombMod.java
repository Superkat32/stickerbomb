package im.f24.stickerbomb;

import im.f24.stickerbomb.items.StickerItem;
import im.f24.stickerbomb.stickers.components.StickerChunkComponent;
import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class StickerBombMod implements ModInitializer {
	public static final String ID = "stickerbomb";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final Identifier STICKER_ITEM_ID = Identifier.of(ID, "sticker");
	public static Item STICKER_ITEM;

	public static ComponentType<Identifier> STICKER_ID = Registry.register(
		Registries.DATA_COMPONENT_TYPE,
		Identifier.of(ID, "sticker_id"),
		ComponentType.<Identifier>builder().codec(Identifier.CODEC).build()
	);

	public static final ComponentKey<StickerChunkComponent> STICKER_CHUNK_COMPONENT_COMPONENT_KEY = ComponentRegistry.getOrCreate(
		Identifier.of(ID, "sticker_chunk_data"),
		StickerChunkComponent.class
	);

	@Override
	public void onInitialize() {
		STICKER_ITEM = registerItem(
			STICKER_ITEM_ID,
			StickerItem::new,
			new Item.Settings()
				.component(STICKER_ID, Identifier.of(ID, "test"))
		);
	}

	public static Item registerItem(Identifier id, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
		Item item = itemFactory.apply(settings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return item;
	}
}
