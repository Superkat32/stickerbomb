package im.f24.stickerbomb.mixin;

import im.f24.stickerbomb.StickerBombMod;
import im.f24.stickerbomb.client.StickerAtlasHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {

	@Shadow
	@Final
	private TextureManager textureManager;
	@Shadow
	@Final
	private ReloadableResourceManagerImpl resourceManager;


	@Inject(at= @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PaintingManager;<init>(Lnet/minecraft/client/texture/TextureManager;)V"), method = "Lnet/minecraft/client/MinecraftClient;<init>(Lnet/minecraft/client/RunArgs;)V")
	public void init(RunArgs args, CallbackInfo ci) {
		StickerAtlasHolder.INSTANCE = new StickerAtlasHolder(this.textureManager);
		resourceManager.registerReloader(StickerAtlasHolder.INSTANCE);

//		StickerAtlasHolder.INSTANCE = new StickerAtlasHolder(MinecraftClient.getInstance().getTextureManager());
//
//		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
//			@Override
//			public Identifier getFabricId() {
//				return Identifier.of(StickerBombMod.ID, "sticker_atlas");
//			}
//
//			@Override
//			public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
//				StickerBombMod.LOGGER.info("reloading sticker atlas");
//				return StickerAtlasHolder.INSTANCE.reload(synchronizer, manager, prepareExecutor, applyExecutor);
//			}
//		});
	}


	@Inject(at= @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PaintingManager;close()V"), method = "Lnet/minecraft/client/MinecraftClient;close()V")
	public void close(CallbackInfo ci) {
		StickerAtlasHolder.INSTANCE.close();
	}
}
