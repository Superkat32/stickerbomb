package im.f24.stickerbomb.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import im.f24.stickerbomb.items.StickerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	
	@Shadow
	public abstract Item getItem();

	@ModifyExpressionValue(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canPlaceOn(Lnet/minecraft/block/pattern/CachedBlockPosition;)Z"))
	public boolean stickerbomb$allowStickersInAdventureMode(boolean original, ItemUsageContext context) {
		if(this.getItem() instanceof StickerItem) {
			return true;
		}

		return original;
	}

}
