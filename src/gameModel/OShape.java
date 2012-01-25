package gameModel;

import java.awt.Color;

public class OShape extends Shape
{

	public OShape() {
		tColor = new Color(240, 240, 0, 200);

		blocks = new Block[4];
		blocks[0] = new Block(tColor);
		blocks[1] = new Block(tColor);
		blocks[2] = new Block(tColor);
		blocks[3] = new Block(tColor);

		pieceRepresentation = new Block[4][4];

		pieceRepresentation[0] = new Block[] { null, blocks[0], blocks[1], null };
		pieceRepresentation[1] = new Block[] { null, blocks[2], blocks[3], null };
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
		
	}

	@Override
	public boolean canRotateLeft(Block[][] boardMatrix) {
		return false;
	}

}
