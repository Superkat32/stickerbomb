package im.f24.stickerbomb.mixin;

import im.f24.stickerbomb.client.StickerAtlasHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
	public void stickerbomb$init(RunArgs args, CallbackInfo ci) {
		StickerAtlasHolder.INSTANCE = new StickerAtlasHolder(this.textureManager);
		resourceManager.registerReloader(StickerAtlasHolder.INSTANCE);
	}


	@Inject(at= @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PaintingManager;close()V"), method = "Lnet/minecraft/client/MinecraftClient;close()V")
	public void stickerbomb$close(CallbackInfo ci) {
		StickerAtlasHolder.INSTANCE.close();
	}
}
