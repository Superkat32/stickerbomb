package im.f24.stickerbomb;

import im.f24.stickerbomb.stickers.StickerWorldManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StickerBombMod implements ModInitializer {
	public static final String ID = "stickerbomb";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);


	@Override
	public void onInitialize() {
		ServerTickEvents.END_WORLD_TICK.register(StickerWorldManager::onWorldTick);

		ServerChunkEvents.CHUNK_LOAD.register(StickerWorldManager::onChunkLoaded);
		ServerChunkEvents.CHUNK_UNLOAD.register(StickerWorldManager::onChunkUnloaded);
	}
}
