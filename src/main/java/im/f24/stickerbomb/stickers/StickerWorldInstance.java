package im.f24.stickerbomb.stickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

/// Represents an in-world sticker.
public class StickerWorldInstance {
	public static final Codec<StickerWorldInstance> CODEC = RecordCodecBuilder.create(
		(instance) ->
			instance.group(
				Identifier.CODEC.fieldOf("texture_id").forGetter(s -> s.stickerTextureId),
				Vec3d.CODEC.fieldOf("position").forGetter(s -> s.position),
				Codec.FLOAT.fieldOf("rotation").forGetter(s -> s.rotation),
				Direction.CODEC.fieldOf("side").forGetter(s -> s.side)
			).apply(instance, StickerWorldInstance::new)
	);

	public static final PacketCodec<ByteBuf, StickerWorldInstance> PACKET_CODEC = new PacketCodec<ByteBuf, StickerWorldInstance>() {
		@Override
		public StickerWorldInstance decode(ByteBuf buf) {
			return new StickerWorldInstance(
				Identifier.PACKET_CODEC.decode(buf),
				Vec3d.PACKET_CODEC.decode(buf),
				buf.readFloat(),
				Direction.PACKET_CODEC.decode(buf)
			);
		}

		@Override
		public void encode(ByteBuf buf, StickerWorldInstance value) {
			Identifier.PACKET_CODEC.encode(buf, value.stickerTextureId);
			Vec3d.PACKET_CODEC.encode(buf, value.position);
			buf.writeFloat(value.rotation);
			Direction.PACKET_CODEC.encode(buf, value.side);
		}
	};

	public Identifier stickerTextureId;
	public Vec3d position;
	public float rotation;
	public Direction side;

	public BlockPos blockPos;
	public Box stickerBox;

	public StickerWorldInstance(Identifier stickerTextureId) {
		this.stickerTextureId = stickerTextureId;
		rotation = 0;
		side = Direction.NORTH;

		setPosition(Vec3d.ZERO);
	}

	private StickerWorldInstance(Identifier stickerTextureId, Vec3d position, float rotation, Direction side) {
		this.stickerTextureId = stickerTextureId;
		this.rotation = rotation;
		this.side = side;

		setPosition(position);
	}


	public void setPosition(Vec3d position) {
		this.position = position;
		this.blockPos = BlockPos.ofFloored(position);

		this.stickerBox = new Box(this.blockPos);
	}

	public ChunkPos getChunkPos() {
		return new ChunkPos(blockPos);
	}
}
