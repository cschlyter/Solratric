package gameModel;

import java.awt.Color;

import utils.SolratricUtils;

public class Block
{

	private Color blockColor;
	private int blockWidth;
	SolratricUtils utils;

	private int x;
	private int y;
	
	private boolean collapsed;

	public Block(Color blockColor) {
		this.blockColor = blockColor;
		
		utils = new SolratricUtils();
		blockWidth = utils.getBlockWidth();
		collapsed = false;
	}

	public Color getBlockColor() {
		return blockColor;
	}

	public void setBlockColor(Color blockColor) {
		this.blockColor = blockColor;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getBlockWidth() {
		return blockWidth;
	}

	public void setBlockWidth(int blockWidth) {
		this.blockWidth = blockWidth;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

}
