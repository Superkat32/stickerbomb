package im.f24.stickerbomb.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StickerEntity extends AbstractDecorationEntity {
	public StickerEntity(EntityType<? extends AbstractDecorationEntity> type, World world) {
		super(type, world);
	}

	@Override
	public void onBreak(ServerWorld world, @Nullable Entity breaker) {

	}


	@Override
	protected Box calculateBoundingBox(BlockPos pos, Direction side) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		return Box.of(vec3d, 1.0, 1.0, 1.0);

	}



	@Override
	public void onPlace() {

	}

	@Override
	public boolean canStayAttached() {
		return true;
	}
}
