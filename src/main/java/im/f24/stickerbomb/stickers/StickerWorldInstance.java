package im.f24.stickerbomb.stickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import im.f24.stickerbomb.items.StickerItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.UUID;

/// Represents an in-world sticker.
public class StickerWorldInstance {
	public static final Codec<StickerWorldInstance> CODEC = RecordCodecBuilder.create(
		(instance) ->
			instance.group(
				Uuids.INT_STREAM_CODEC.fieldOf("sticker_id").forGetter(s -> s.id),
				Identifier.CODEC.fieldOf("texture_id").forGetter(s -> s.stickerTextureId),
				Vec3d.CODEC.fieldOf("position").forGetter(s -> s.position),
				Codec.FLOAT.fieldOf("rotation").forGetter(s -> s.rotation),
				Codec.FLOAT.fieldOf("scale").forGetter(s -> s.scale),
				Direction.CODEC.fieldOf("side").forGetter(s -> s.side)
			).apply(instance, StickerWorldInstance::new)
	);

	public static final PacketCodec<ByteBuf, StickerWorldInstance> PACKET_CODEC = new PacketCodec<>() {
		@Override
		public StickerWorldInstance decode(ByteBuf buf) {
			return new StickerWorldInstance(
				Uuids.PACKET_CODEC.decode(buf),
				Identifier.PACKET_CODEC.decode(buf),
				Vec3d.PACKET_CODEC.decode(buf),
				buf.readFloat(),
				buf.readFloat(),
				Direction.PACKET_CODEC.decode(buf)
			);
		}

		@Override
		public void encode(ByteBuf buf, StickerWorldInstance value) {
			Uuids.PACKET_CODEC.encode(buf, value.id);
			Identifier.PACKET_CODEC.encode(buf, value.stickerTextureId);
			Vec3d.PACKET_CODEC.encode(buf, value.position);
			buf.writeFloat(value.rotation);
			buf.writeFloat(value.scale);
			Direction.PACKET_CODEC.encode(buf, value.side);
		}
	};

	public UUID id;
	public Identifier stickerTextureId;
	public Vec3d position;
	public float rotation;
	public float scale;
	public Direction side;

	public BlockPos blockPos;
	public Box stickerBox;

	public StickerWorldInstance(Identifier stickerTextureId) {
		this.id = UUID.randomUUID();
		this.stickerTextureId = stickerTextureId;
		rotation = 0;
		scale = 1;
		side = Direction.NORTH;

		setPosition(Vec3d.ZERO);
	}

	private StickerWorldInstance(UUID id, Identifier stickerTextureId, Vec3d position, float rotation, float scale, Direction side) {
		this.id = id;
		this.stickerTextureId = stickerTextureId;
		this.rotation = rotation;
		this.scale = scale;
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


	public ItemEntity createItemEntity(World world) {
		var pos = position.add(side.getDoubleVector().multiply(0.5f));
		var entity = new ItemEntity(world, pos.x, pos.y, pos.z, StickerItem.stackWithId(stickerTextureId));
		entity.setVelocity(side.getDoubleVector().multiply(0.3f));
		return entity;
	}

	public static boolean isBlockPlacementValid(BlockPos onBlockPos, Direction side, World world) {
		var onBlock = world.getBlockState(onBlockPos);
		var inBlock = world.getBlockState(onBlockPos.offset(side));

		return onBlock.isSideSolidFullSquare(world, onBlockPos, side) &&
			!inBlock.isSolidBlock(world, onBlockPos.offset(side));
	}
}
