package gameModel;


import java.awt.Color;

public class ZShape extends Shape
{

	public ZShape() {
		super();

		tColor = new Color(255, 0, 0, 200);

		blocks = new Block[4];
		blocks[0] = new Block(tColor);
		blocks[1] = new Block(tColor);
		blocks[2] = new Block(tColor);
		blocks[3] = new Block(tColor);

		pieceRepresentation = new Block[4][4];

		// 0 1 1 0
		// 0 0 1 1
		// 0 0 0 0
		// 0 0 0 0
		pieceRepresentation[0] = new Block[] { null, blocks[0], blocks[1], null };
		pieceRepresentation[1] = new Block[] { null, null, blocks[2], blocks[3] };
		pieceRepresentation[2] = new Block[] { null, null, null, null };
		pieceRepresentation[3] = new Block[] { null, null, null, null };
	}

	public void rotateRight() {

	}

	@Override
	public boolean canRotateRight(Block[][] boardMatrix) {

		return false;
	}

	@Override
	public void rotateLeft() {
		if (rotationState == RotationState.NORMAL) {

			moveBlocksPosition(0, 1, 1, -1, 0, 0, 1, -2);
			rotationState = RotationState.LEFT;
		}
		else if (rotationState == RotationState.LEFT) {

			moveBlocksPosition(1, -1, 0, 1, 1, 0, 0, 2);
			rotationState = RotationState.DOWN;
		}
		else if (rotationState == RotationState.DOWN) {

			moveBlocksPosition(-1, 2, 0, 0, -1, 1, 0, -1);
			rotationState = RotationState.RIGHT;
		}
		else if (rotationState == RotationState.RIGHT) {

			moveBlocksPosition(0, -2, -1, 0, 0, -1, -1, 1);
			rotationState = RotationState.NORMAL;
		}

	}

	@Override
	public boolean canRotateLeft(Block[][] boardMatrix) {
		
		boolean canMove = true;

		switch (rotationState) {
		case NORMAL:
			canMove = checkCanMove(boardMatrix, 1, 0, 2, -1, -1, 1, -1, 0);
			return canMove;
		case LEFT:
			canMove = checkCanMove(boardMatrix, 0, 1, 2, 1, 1, 1, 0, -1);
			return canMove;
		case DOWN:
			canMove = checkCanMove(boardMatrix, -1, 0, -2, 1, 1, 1, 1, 0);
			return canMove;
		case RIGHT:
			canMove = checkCanMove(boardMatrix, 0, -1, -1, -2, -1, -1, 0, 1);
			return canMove;

		default:
			return false;
		}

	}

}
