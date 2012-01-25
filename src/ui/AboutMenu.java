package ui;

import java.awt.Graphics;

public class AboutMenu
{
	
	public void show(Graphics gfx) {
		
		int y = 240;
		int ySpacing = 30;
		int x = 200;
		
		gfx.drawString("ABOUT", 370, y - 40);
		gfx.drawString("Solratric is an open-source", x, y);
		gfx.drawString("tetris clone written in Java.", x, y + ySpacing);
		gfx.drawString("Its source code is available at:", x, y + (ySpacing * 2));
		gfx.drawString("http://github.com/cschlyter/Solratric", x, y + (ySpacing * 3));
	}

}
