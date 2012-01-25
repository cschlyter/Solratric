package gameModel;


import java.awt.Color;

public class LShape extends Shape
{

	public LShape() {
		tColor = new Color(240, 160, 0, 200);

		blocks = new Block[4];
		blocks[0] = new Block(tColor);
		blocks[1] = new Block(tColor);
		blocks[2] = new Block(tColor);
		blocks[3] = new Block(tColor);

		pieceRepresentation = new Block[4][4];

//		0 0 0 1
//		0 1 1 1
//		0 0 0 0
//		0 0 0 0
		pieceRepresentation[0] = new Block[] { null, null, null, blocks[3]};
		pieceRepresentation[1] = new Block[] { null, blocks[0], blocks[1], blocks[2] };
		pieceRepresentation[2] = new Block[] { null, null, null, null };
		pieceRepresentation[3] = new Block[] { null, null, null, null };
	}

	@Override
	public void rotateRight() {

	}

	@Override
	public boolean canRotateRight(Block[][] boardMatrix) {
		return false;
	}

	@Override
	public void rotateLeft() {
		
		if (rotationState == RotationState.NORMAL) {

			moveBlocksPosition(0, -2, -1, 1, 0, 0, 1, -1);
			rotationState = RotationState.LEFT;
		}
		else if (rotationState == RotationState.LEFT) {

			moveBlocksPosition(1, 0, 1, 0, 0, 1, 0, -1);
			rotationState = RotationState.DOWN;
		}
		else if (rotationState == RotationState.DOWN) {

			moveBlocksPosition(-1, 1, 0, 0, 1, -1, 0, 2);
			rotationState = RotationState.RIGHT;
		}
		else if (rotationState == RotationState.RIGHT) {

			moveBlocksPosition(0, 1, 0, -1, -1, 0, -1, 0);
			rotationState = RotationState.NORMAL;
		}
		
	}

	@Override
	public boolean canRotateLeft(Block[][] boardMatrix) {
		
		boolean canMove = true;

		switch (rotationState) {
		case NORMAL:
			canMove = checkCanMove(boardMatrix, 0, -2, -1, 1, 1, -1, 1, -1);
			return canMove;
		case LEFT:
			canMove = checkCanMove(boardMatrix, 1, 0, 1, 1, 1, -1, 0, 1);
			return canMove;
		case DOWN:
			canMove = checkCanMove(boardMatrix, -1, 1, -1, 1, 1, -1, 0, 2);
			return canMove;
		case RIGHT:
			canMove = checkCanMove(boardMatrix, 0, -1, -1, 1, -1, -1, -1, 0);
			return canMove;

		default:
			return false;
		}
	}

}
