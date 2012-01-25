package ui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class GUIUtils
{
	
	public Stroke changeDrawRectThickness(Graphics gfx) {
		double thickness = 3;
		Stroke oldStroke = ((Graphics2D) gfx).getStroke();
		((Graphics2D) gfx).setStroke(new BasicStroke((float) thickness));

		return oldStroke;
	}

}
