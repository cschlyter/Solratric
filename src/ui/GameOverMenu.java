package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class GameOverMenu
{

	public enum GameOverMenuState {
		PlayAgainSelected, QuitSelected, AboutSelected, AboutOpen
	}

	GameOverMenuState menuState;
	GUIUtils guiUtils;
	AboutMenu aboutMenu;

	public GameOverMenu() {
		menuState = GameOverMenuState.PlayAgainSelected;
		guiUtils = new GUIUtils();
		aboutMenu = new AboutMenu();
	}

	public GameOverMenuState getMenuState() {
		return menuState;
	}

	public void setMenuState(GameOverMenuState menuState) {
		this.menuState = menuState;
	}

	public void show(Graphics gfx) {
		
		if (menuState == GameOverMenuState.AboutOpen) {
			aboutMenu.show(gfx);
		}
		else {
			int y = 260;
			int ySpacing = 30;
			int x = 315;

			if (menuState == GameOverMenuState.PlayAgainSelected) {
				gfx.drawString("->", x, y);
			}
			else if (menuState == GameOverMenuState.QuitSelected) {
				gfx.drawString("->", x, y + ySpacing);
			}
			else if (menuState == GameOverMenuState.AboutSelected) {
				gfx.drawString("->", x, y + (ySpacing * 2));
			}

			gfx.drawString("GAME OVER", x + 27, y - 40);
			gfx.drawString("Play Again", x + 30, y);
			gfx.drawString("Quit", x + 60, y + ySpacing);
			gfx.drawString("About", x + 54, y + (ySpacing * 2));

			Stroke oldStroke = guiUtils.changeDrawRectThickness(gfx);
			gfx.drawRect(305, y - ySpacing - 50, 200, 183);
			((Graphics2D) gfx).setStroke(oldStroke);
		}
	}

	public void handleMenuStateMovingUp() {
		switch (menuState) {
		case PlayAgainSelected:
			menuState = GameOverMenuState.AboutSelected;
			break;
		case AboutSelected:
			menuState = GameOverMenuState.QuitSelected;
			break;
		case QuitSelected:
			menuState = GameOverMenuState.PlayAgainSelected;
			break;
		}
	}

	public void handleMenuStateMovingDown() {
		switch (menuState) {
		case PlayAgainSelected:
			menuState = GameOverMenuState.QuitSelected;
			break;
		case QuitSelected:
			menuState = GameOverMenuState.AboutSelected;
			break;
		case AboutSelected:
			menuState = GameOverMenuState.PlayAgainSelected;
			break;
		}
	}

}
