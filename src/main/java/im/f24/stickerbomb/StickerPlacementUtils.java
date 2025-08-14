package im.f24.stickerbomb;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class StickerPlacementUtils {


	public static float getRotationForPlacement(Vec3d stickerPosition, Vec3d headPosition, Direction side) {
		var dir = stickerPosition.subtract(headPosition).normalize();
		var vec = new Vec2f(0, 0);

		switch (side) {
			case DOWN:
				vec = new Vec2f(-(float) dir.x, (float) dir.z);
				break;
			case UP:
				vec = new Vec2f((float) dir.x, (float) dir.z);
				break;
			case NORTH:
				vec = new Vec2f(-(float)dir.x, -1).normalize();
				break;
			case SOUTH:
				vec = new Vec2f((float)dir.x, -1).normalize();
				break;
			case EAST:
				vec = new Vec2f(-(float)dir.z, -1).normalize();
				break;
			case WEST:
				vec = new Vec2f((float)dir.z, -1).normalize();
				break;
		}


		var angle = Math.atan2(vec.y, -vec.x);


		return (float) (angle + Math.PI * 0.5f);
	}
}
