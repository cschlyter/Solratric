package gameModel;

import java.awt.Color;

public abstract class Shape
{
	
	public enum RotationState {
		NORMAL, RIGHT, DOWN, LEFT
	}

	protected Block[][] pieceRepresentation;
	protected Color tColor;
	protected Block[] blocks;
	protected RotationState rotationState;

	public Shape() {

		rotationState = RotationState.NORMAL;
	}

	public Block[][] getPieceRepresentation() {
		return pieceRepresentation;
	}

	public void setPieceRepresentation(Block[][] pieceRepresentation) {
		this.pieceRepresentation = pieceRepresentation;
	}

	protected void moveBlocksPosition(int firstBlockX, int firstBlockY, int secBlockX, int secBlockY, int thirdBlockX, int thirdBlockY, int fourthBlockX,
			int fourthBlockY)
	{
		for (int i = 0; i < getBlocks().length; i++) {
			Block tempBlock = getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();

			if (i == 0)
				setBlockPosition(x + firstBlockX, y + firstBlockY, 0);

			if (i == 1)
				setBlockPosition(x + secBlockX, y + secBlockY, 1);

			if (i == 2)
				setBlockPosition(x + thirdBlockX, y + thirdBlockY, 2);

			if (i == 3)
				setBlockPosition(x + fourthBlockX, y + fourthBlockY, 3);

		}
	}

	public void setBlockPosition(int x, int y, int blockPosition) {
		blocks[blockPosition].setX(x);
		blocks[blockPosition].setY(y);
	}

	public boolean shapeHasPosition(int x, int y) {

		boolean hasPosition = false;
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].getX() == x && blocks[i].getY() == y) {
				hasPosition = true;
			}
		}
		return hasPosition;
	}

	public boolean checkCanMove(Block[][] boardMatrix, int firstBlockX, int firstBlockY, int secBlockX, int secBlockY, int thirdBlockX, int thirdBlockY,
			int fourthBlockX, int fourthBlockY)
	{
		for (int i = 0; i < getBlocks().length; i++) {
			Block tempBlock = getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();

			// XXX Handle array bounds.
			// int boardWidth = boardCons.getBoardWidth();
			// if(y + thirdBlockY >= boardWidth || y + firstBlockY >=
			// boardWidth)
			// return false;

			// if(y + thirdBlockY < 0 || y + firstBlockY < 0)
			// return false;

			if (i == 0) {
				if (boardMatrix[x + firstBlockX][y + firstBlockY] != null)
					return false;
			}

			if (i == 1) {
				if (boardMatrix[x + secBlockX][y + secBlockY] != null)
					return false;
			}

			if (i == 2) {
				if (boardMatrix[x + thirdBlockX][y + thirdBlockY] != null)
					return false;
			}

			if (i == 3) {
				if (boardMatrix[x + fourthBlockX][y + fourthBlockY] != null)
					return false;
			}
		}

		return true;
	}

	public Block[] getBlocks() {
		return blocks;
	}

	public void setBlocks(Block[] blocks) {
		this.blocks = blocks;
	}

	abstract public void rotateRight();

	abstract public boolean canRotateRight(Block[][] boardMatrix);

	abstract public void rotateLeft();

	abstract public boolean canRotateLeft(Block[][] boardMatrix);

}