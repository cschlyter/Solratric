package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class GameMainWindow extends JFrame implements WindowListener, IGameView
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int DEFAULT_FPS = 60;

	private GamePanel gamePanel; // where the worm is drawn
	private BorderLayout borderLayout;
	int period;

	public GameMainWindow(int period) {
		super("Solratric!");
		
		ImageIcon img = new ImageIcon(this.getClass().getResource("/images/icon.png"));
		this.setIconImage(img.getImage());
		
		this.period = period;
		startGame(period);
	}
	
	public void startGame(int period) {
		
		if (gamePanel != null) {
			this.remove(gamePanel);
		}
		
		borderLayout = new BorderLayout();
		setResizable(false);
		
		requestFocus();
		makeGUI(period);
		gamePanel.requestFocus();
		
		addWindowListener(this);
		setVisible(true);
		pack();
	}

	private void makeGUI(int period) {
		Container c = getContentPane();

		gamePanel = new GamePanel(this, period);
		c.add(gamePanel, BorderLayout.CENTER);

	}

	public void windowActivated(WindowEvent e) {
		gamePanel.resumeGame();
	}

	public void windowDeactivated(WindowEvent e) {
		gamePanel.pauseGame();
	}

	public void windowDeiconified(WindowEvent e) {
		gamePanel.resumeGame();
	}

	public void windowIconified(WindowEvent e) {
		gamePanel.pauseGame();
	}

	public void windowClosing(WindowEvent e) {
		gamePanel.stopGame();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
	
	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);

		int period = (int) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");

		new GameMainWindow(period); // ms
	}

}
