package im.f24.stickerbomb;

import im.f24.stickerbomb.blocks.StickerPrinterBlock;
import im.f24.stickerbomb.items.StickerItem;
import im.f24.stickerbomb.items.StickerScraper;
import im.f24.stickerbomb.network.PrinterScreenProvideIDC2SPayload;
import im.f24.stickerbomb.screen.PrinterScreenHandler;
import im.f24.stickerbomb.stickers.components.StickerChunkComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class StickerBombMod implements ModInitializer {
	public static final String ID = "stickerbomb";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final int STICKER_PIXELS_PER_BLOCK = 32;

	public static Item BLANK_STICKER_ITEM;
	public static Item STICKER_ITEM;
	public static Item STICKER_SCRAPER_ITEM;

	public static Block STICKER_PRINTER_BLOCK;

	public static ScreenHandlerType<PrinterScreenHandler> PRINTER_SCREEN_HANDLER;

	public static final ComponentKey<StickerChunkComponent> STICKER_CHUNK_COMPONENT_COMPONENT_KEY = ComponentRegistry.getOrCreate(
		Identifier.of(ID, "sticker_chunk_data"),
		StickerChunkComponent.class
	);


	@Override
	public void onInitialize() {

		BLANK_STICKER_ITEM = registerItem(
			Identifier.of(ID, "blank_sticker"),
			Item::new,
			new Item.Settings()
		);

		STICKER_ITEM = registerItem(
			Identifier.of(ID, "sticker"),
			StickerItem::new,
			new Item.Settings()
				.component(StickerItem.STICKER_DATA_COMPONENT, StickerItem.StickerItemData.DEFAULT)
		);

		STICKER_SCRAPER_ITEM = registerItem(
			Identifier.of(ID, "scraper"),
			StickerScraper::new,
			new Item.Settings()
		);

		STICKER_PRINTER_BLOCK = registerBlock(
			Identifier.of(ID, "printer"),
			StickerPrinterBlock::new,
			AbstractBlock.Settings.create(),
			true
		);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(group -> {
			group.add(STICKER_PRINTER_BLOCK);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(group -> {
			group.add(BLANK_STICKER_ITEM);
			group.add(STICKER_SCRAPER_ITEM);
		});

		PRINTER_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ID, "printer"),
			new ScreenHandlerType<>(PrinterScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

		PayloadTypeRegistry.playC2S().register(PrinterScreenProvideIDC2SPayload.ID, PrinterScreenProvideIDC2SPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(PrinterScreenProvideIDC2SPayload.ID, (payload, context) -> {
			if (!(context.player().currentScreenHandler instanceof PrinterScreenHandler printerScreenHandler)) return;
			printerScreenHandler.setStickerId(payload.id());
		});
	}

	private static Item registerItem(Identifier id, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		var itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
		var item = itemFactory.apply(settings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return item;
	}

	private static Block registerBlock(Identifier id, Function<AbstractBlock.Settings, Block> blockFactory, Block.Settings settings, boolean shouldRegisterItem) {
		var key = RegistryKey.of(RegistryKeys.BLOCK, id);
		var block = blockFactory.apply(settings.registryKey(key));

		if (shouldRegisterItem) {
			var itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
			var item = new BlockItem(block, new Item.Settings().registryKey(itemKey));
			Registry.register(Registries.ITEM, itemKey, item);
		}

		return Registry.register(Registries.BLOCK, key, block);
	}

	private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(
		Identifier id,
		FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
		Block... blocks
	) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
	}
}
