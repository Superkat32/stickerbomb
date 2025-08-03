package im.f24.stickerbomb.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class StickerContainerEntity extends Entity {
	public static final TrackedDataHandler<List<StickerInstance>> STICKER_LIST_HANDLER = TrackedDataHandler.create(StickerInstance.PACKET_CODEC.collect(PacketCodecs.toList()));
	private static final TrackedData<List<StickerInstance>> STICKER_LIST = DataTracker.registerData(StickerContainerEntity.class, STICKER_LIST_HANDLER);

	public final List<StickerInstance> stickerList = new ArrayList<>();

	public StickerContainerEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(STICKER_LIST, new ArrayList<>());
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void readCustomData(ReadView view) {
		this.stickerList.clear();
		this.dataTracker.set(STICKER_LIST, view.getTypedListView("stickers", StickerInstance.CODEC).stream().toList(), true);
	}

	@Override
	protected void writeCustomData(WriteView view) {
		view.put("stickers", StickerInstance.CODEC.listOf(), this.stickerList);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);

		if (data.equals(STICKER_LIST)) {
			this.stickerList.clear();
			this.stickerList.addAll(this.dataTracker.get(STICKER_LIST));
		}
	}

	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(
			Math.floor(x / 16) * 16 + 8,
			Math.floor(y / 16) * 16 + 8,
			Math.floor(z / 16) * 16 + 8
		);
	}

	@Override
	public boolean canHit() {
		return false;
	}

	public void addSticker(StickerInstance stickerInstance) {
		this.stickerList.add(stickerInstance);
		this.dataTracker.set(STICKER_LIST, this.stickerList, true);
	}

	public record StickerInstance(Identifier id, Vec3d offset, float rotation, Direction side) {
		public static final Codec<StickerInstance> CODEC = RecordCodecBuilder.create(
			(instance) ->
				instance.group(
					Identifier.CODEC.fieldOf("id").forGetter(StickerInstance::id),
					Vec3d.CODEC.fieldOf("offset").forGetter(StickerInstance::offset),
					Codec.FLOAT.fieldOf("rotation").forGetter(StickerInstance::rotation),
					Direction.CODEC.fieldOf("side").forGetter(StickerInstance::side)
				).apply(instance, StickerInstance::new)
		);

		public static final PacketCodec<ByteBuf, StickerInstance> PACKET_CODEC = new PacketCodec<>() {
			@Override
			public StickerInstance decode(ByteBuf buf) {
				return new StickerInstance(
					Identifier.PACKET_CODEC.decode(buf),
					Vec3d.PACKET_CODEC.decode(buf),
					PacketCodecs.FLOAT.decode(buf),
					Direction.PACKET_CODEC.decode(buf)
				);
			}

			@Override
			public void encode(ByteBuf buf, StickerInstance value) {
				Identifier.PACKET_CODEC.encode(buf, value.id);
				Vec3d.PACKET_CODEC.encode(buf, value.offset);
				PacketCodecs.FLOAT.encode(buf, value.rotation);
				Direction.PACKET_CODEC.encode(buf, value.side);
			}
		};
	}
}
