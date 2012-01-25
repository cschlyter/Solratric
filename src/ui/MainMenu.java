package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class MainMenu
{

	public enum MenuState {
		ResumeSelected, QuitSelected, AboutSelected, AboutOpen
	}

	MenuState menuState;
	GUIUtils guiUtils;
	AboutMenu aboutMenu;

	public MainMenu() {
		menuState = MenuState.ResumeSelected;
		guiUtils = new GUIUtils();
		aboutMenu = new AboutMenu();
	}

	public MenuState getMenuState() {
		return menuState;
	}

	public void setMenuState(MenuState menuState) {
		this.menuState = menuState;
	}

	public void show(Graphics gfx) {

		if (menuState == MenuState.AboutOpen) {
			aboutMenu.show(gfx);
		}
		else {
			int y = 240;
			int ySpacing = 30;

			if (menuState == MenuState.ResumeSelected) {
				gfx.drawString("->", 317, y);
			}
			else if (menuState == MenuState.QuitSelected) {
				gfx.drawString("->", 317, y + ySpacing);
			}
			else if (menuState == MenuState.AboutSelected) {
				gfx.drawString("->", 317, y + (ySpacing * 2));
			}

			gfx.drawString("Resume", 362, y);
			gfx.drawString("Quit", 378, y + ySpacing);
			gfx.drawString("About", 370, y + (ySpacing * 2));

			Stroke oldStroke = guiUtils.changeDrawRectThickness(gfx);
			gfx.drawRect(305, y - ySpacing - 10, 201, 126);
			((Graphics2D) gfx).setStroke(oldStroke);
		}
	}

//	public void handleMenuResult() {
//
//		switch (menuState) {
//		case ResumeSelected:
//			menuResult = MenuResult.Resume;
//			break;
//		case QuitSelected:
//			menuResult = MenuResult.Quit;
//			break;
//		case AboutSelected:
//			menuResult = MenuResult.About;
//			break;
//		}
//	}

	public void handleMenuStateMovingUp() {

		switch (menuState) {
		case ResumeSelected:
			menuState = MenuState.AboutSelected;
			break;
		case AboutSelected:
			menuState = MenuState.QuitSelected;
			break;
		case QuitSelected:
			menuState = MenuState.ResumeSelected;
			break;
		}
	}

	public void handleMenuStateMovingDown() {

		switch (menuState) {
		case ResumeSelected:
			menuState = MenuState.QuitSelected;
			break;
		case QuitSelected:
			menuState = MenuState.AboutSelected;
			break;
		case AboutSelected:
			menuState = MenuState.ResumeSelected;
			break;
		}
	}

}
