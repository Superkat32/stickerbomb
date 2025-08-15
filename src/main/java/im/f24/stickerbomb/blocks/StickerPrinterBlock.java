package im.f24.stickerbomb.blocks;

import im.f24.stickerbomb.screen.PrinterScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StickerPrinterBlock extends Block {

	public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty TEMPORARY = BooleanProperty.of("temp");

	public StickerPrinterBlock(Settings settings) {
		super(settings);

		this.setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(TEMPORARY);
	}

	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		var isTemp = false;
		var stack = ctx.getStack();
		var customData = stack.get(DataComponentTypes.CUSTOM_DATA);

		if (customData != null) {
			var nbt = customData.getNbt();
			if (nbt != null && nbt.contains("temp")) {
				isTemp = nbt.getBoolean("temp").get();
			}
		}

		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(TEMPORARY, isTemp);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient) {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		}

		return ActionResult.SUCCESS;
	}

	@Override
	protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(syncId, playerInventory, player) -> new PrinterScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos)),
			Text.translatable("screen.stickerbomb.printer")
		);
	}


	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(1 / 16.0f, 0 / 16.0f, 1 / 16.0f, 15 / 16.0f, 5 / 16.0f, 15 / 16.0f);
	}
}
