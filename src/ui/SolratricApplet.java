package ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;

public class SolratricApplet extends JApplet implements IGameView
{

	private static final int DEFAULT_FPS = 80;

	private GamePanel wp; // where the worm is drawn

	public void init() {
		int fps = DEFAULT_FPS;

		int period = (int) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");

		makeGUI(period);
		wp.requestFocus();
	}

	@Override
	public void startGame(int period) {

	}

	private void makeGUI(int period) {
		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		if (wp != null) {
			c.getLayout().removeLayoutComponent(wp);
		}

		wp = new GamePanel(this, period);
		c.add(wp, "Center");
	}

	// -------------------- applet life cycle methods --------------

	public void start() {
		wp.resumeGame();
	}

	public void stop() {
		wp.pauseGame();
	}

	public void destroy() {
		wp.stopGame();
	}
}
