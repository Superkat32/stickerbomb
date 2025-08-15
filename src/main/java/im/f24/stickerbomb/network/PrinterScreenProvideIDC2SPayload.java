package im.f24.stickerbomb.network;

import im.f24.stickerbomb.StickerBombMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record PrinterScreenProvideIDC2SPayload(Optional<Identifier> id) implements CustomPayload {
	public static final Identifier IDENTIFIER = Identifier.of(StickerBombMod.ID, "printer_screen_set_id");
	public static final CustomPayload.Id<PrinterScreenProvideIDC2SPayload> ID = new CustomPayload.Id<>(IDENTIFIER);
	public static final PacketCodec<RegistryByteBuf, PrinterScreenProvideIDC2SPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Identifier.PACKET_CODEC),
		PrinterScreenProvideIDC2SPayload::id,
		PrinterScreenProvideIDC2SPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
