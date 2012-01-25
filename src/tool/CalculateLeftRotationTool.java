package tool;

import gameModel.Block;
import gameModel.Shape;
import gameModel.ZShape;

import java.awt.Color;

import utils.SolratricUtils;

public class CalculateLeftRotationTool
{

	protected Block[][] pieceRepresentation;
	protected Block[][] pieceRepresentation2;
	protected Block[] blocks;
	protected Block[] blocks2;

	public Shape currentShape;
	public Shape currentShape2;
	private Block[][] boardMatrix;
	private Block[][] boardMatrix2;

	public CalculateLeftRotationTool() {

		setup();

		calculatePositionDiff();

		printShapeCreationTemplate();
	}
	
	public void printShapeCreationTemplate() {

		int[][] intTemplate;
		intTemplate = new int[4][4];
		intTemplate[0] = new int[] { 0, 0, 1, 0 };
		intTemplate[1] = new int[] { 0, 1, 1, 1 };
		intTemplate[2] = new int[] { 0, 0, 0, 0 };
		intTemplate[3] = new int[] { 0, 0, 0, 0 };

		for (int i = 0; i < intTemplate.length; i++) {
			printLine(intTemplate[i], i);
		}
	}

	private void setup() {

		SolratricUtils utils = new SolratricUtils();
		
		int boardHeigth = utils.getBoardHeigth();
		int boardWidth = utils.getBoardWidth();
		
		boardMatrix = new Block[boardHeigth][boardWidth];
		blocks = new Block[4];
		blocks[0] = new Block(Color.YELLOW);
		blocks[1] = new Block(Color.YELLOW);
		blocks[2] = new Block(Color.YELLOW);
		blocks[3] = new Block(Color.YELLOW);

		pieceRepresentation = new Block[4][4];

		////////////////		
		
		pieceRepresentation[0] = new Block[] { null, null, blocks[1], null};
		pieceRepresentation[1] = new Block[] { null, null, blocks[2], blocks[0]};
		pieceRepresentation[2] = new Block[] { null, null, blocks[3], null};
		pieceRepresentation[3] = new Block[] { null, null, null, null};

		currentShape = new ZShape();
		currentShape.setPieceRepresentation(pieceRepresentation);
		currentShape.setBlocks(blocks);

		boardMatrix2 = new Block[boardHeigth][boardWidth];
		blocks2 = new Block[4];
		blocks2[0] = new Block(Color.YELLOW);
		blocks2[1] = new Block(Color.YELLOW);
		blocks2[2] = new Block(Color.YELLOW);
		blocks2[3] = new Block(Color.YELLOW);
		pieceRepresentation2 = new Block[4][4];
	
		//////////////
		
		pieceRepresentation2[0] = new Block[] { null, null, blocks2[0], null};
		pieceRepresentation2[1] = new Block[] { null, blocks2[1], blocks2[2], blocks2[3]};
		pieceRepresentation2[2] = new Block[] { null, null, null, null};
		pieceRepresentation2[3] = new Block[] { null, null, null, null};

		currentShape2 = new ZShape();
		currentShape2.setPieceRepresentation(pieceRepresentation2);
		currentShape2.setBlocks(blocks2);

		placeShapeInInicialPos();
		placeShapeInInicialPos2();
	}

	private void printLine(int[] line, int lineNumber) {

		String result = "pieceRepresentation2[" + lineNumber + "] = new Block[] {";

		for (int i = 0; i < 4; i++) {

			if (line[i] == 0) {
				result += " null,";
			}
			if (line[i] == 1) {
				result += " blocks2[0],";
			}

		}
		result.trim();
		int l = result.length();

		String result2 = result.substring(0, l - 1);
		result2 += "};";
		System.out.println(result2);
	}

	public void calculatePositionDiff() {

		String result = "moveBlocksPosition(";

		for (int i = 0; i < 4; i++) {

			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();

			Block tempBlock2 = currentShape2.getBlocks()[i];
			int x2 = tempBlock2.getX();
			int y2 = tempBlock2.getY();

			result += (x2 - x) + ", " + (y2 - y) + ", ";
		}
		result.trim();
		int l = result.length();

		String result2 = result.substring(0, l - 2);
		result2 += ");";
		System.out.println(result2);

	}

	private void placeShapeInInicialPos() {

		int count = 0;
		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 4; k++) {

				if (currentShape.getPieceRepresentation()[i][k] != null) {
					boardMatrix[i][k + 2] = pieceRepresentation[i][k];
					currentShape.setBlockPosition(i, k + 2, count);

					count++;
				}
			}
		}
	}

	private void placeShapeInInicialPos2() {

		int count = 0;
		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 4; k++) {

				if (currentShape2.getPieceRepresentation()[i][k] != null) {
					boardMatrix2[i][k + 2] = pieceRepresentation2[i][k];
					currentShape2.setBlockPosition(i, k + 2, count);

					count++;
				}
			}
		}
	}

}
