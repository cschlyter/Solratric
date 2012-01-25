package ui;

import gameModel.Block;
import gameModel.IShape;
import gameModel.JShape;
import gameModel.LShape;
import gameModel.OShape;
import gameModel.SShape;
import gameModel.Shape;
import gameModel.Sound;
import gameModel.TShape;
import gameModel.ZShape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.JPanel;

import ui.GameOverMenu.GameOverMenuState;
import ui.MainMenu.MenuState;
import utils.ImagesLoader;
import utils.SolratricUtils;

public class GamePanel extends JPanel implements Runnable
{
	// TODO add music
	private int boardWidthInPixels;
	private int boardHeigthInPixels;
	private static final String IMS_INFO = "imsInfo.txt";

	private static long MAX_STATS_INTERVAL = 1000L;
	// record stats every 1 second (roughly)

	private static final int NO_DELAYS_PER_YIELD = 16;
	/*
	 * Number of frames with a delay of 0 ms before the animation thread yields
	 * to other running threads.
	 */

	private static int MAX_FRAME_SKIPS = 5; // was 2;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered

	private static int NUM_FPS = 10;
	// number of FPS values stored to get an average

	// used for gathering statistics
	private long statsInterval = 0L; // in ms
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	private int timeSpentInGame = 0; // in seconds

	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private DecimalFormat timedf = new DecimalFormat("0.####"); // 4 dp

	private Thread animator; // the thread that performs the animation
	private volatile boolean running = false; // used to stop the animation
												// thread
	private volatile boolean isPaused = false;

	private int period; // period between drawing in _ms_

	private IGameView wcTop;
	private Shape currentShape;
	private Block[][] boardMatrix;

	private Font debugFont;
	GUIUtils guiUtils;

	// off screen rendering
	private Graphics boardGraphics;
	private ui.MainMenu mainMenu;
	private GameOverMenu gameOverMenu;
	private Image dbImage = null;
	private long lastUpdateTime = 0;
	private long lastUpdateTimeCollapse = 0;
	private long lockDelayLastUpdateTime = 0;
	private long frameTime = 300;
	protected boolean spacePressed;
	private boolean gameOver = false;
	private boolean goStraightDown;
	protected boolean canMove;
	private SolratricUtils utils;
	private boolean playSounds;

	private int blockWidth;
	private int boardWidth;
	private int boardHeigth;
	private int windowWidth;
	private int windowHeigth;

	private int score;
	private int linesBeingCollapsed;
	private int level;
	private BufferedImage bgImage;
	private int padding;
	Color linesColor;
	Font font;

	public GamePanel(IGameView wc, int period) {

		linesColor = new Color(7, 14, 140, 255);
		wcTop = wc;
		this.period = period;
		padding = 280;

		mainMenu = new ui.MainMenu();
		gameOverMenu = new GameOverMenu();
		ImagesLoader imsLoader = new ImagesLoader(IMS_INFO);
		bgImage = imsLoader.getImage("background");
		guiUtils = new GUIUtils();

		score = 0;
		linesBeingCollapsed = 0;
		canMove = true;
		utils = new SolratricUtils();
		playSounds = utils.getSoundProperties();
		level = 0;

		blockWidth = utils.getBlockWidth();
		boardWidth = utils.getBoardWidth();
		boardHeigth = utils.getBoardHeigth();

		boardWidthInPixels = blockWidth * boardWidth;
		boardHeigthInPixels = blockWidth * boardHeigth;

		// XXX use window size acording to block width
		windowWidth = utils.getWindowWidth();
		windowHeigth = utils.getWindowHeigth();

		setBackground(Color.white);
		setPreferredSize(new Dimension(windowWidth, windowHeigth));
		setMinimumSize(new Dimension(windowWidth, windowHeigth));

		setFocusable(true);
		requestFocus();
		readyForTermination();

		moveShapeListeners();
		pauseListener();

		currentShape = selectRandomShape();
		// currentShape = new TShape();

		boardMatrix = new Block[boardHeigth][boardWidth];

		debugFont = new Font("Tahoma", Font.PLAIN, 12);

		font = utils.getFont("dlxfont.ttf");

		font = new Font(font.getFontName(), font.getStyle(), 15);
		// initialise timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}
	}

	private void pauseListener() {
		addKeyListener(new KeyAdapter() {

			// listen for arrows keys and space
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == KeyEvent.VK_ENTER) {

					if (isPaused) {
						MenuState menuState = mainMenu.getMenuState();
						
						switch (menuState) {
						case ResumeSelected:
							resumeGame();
							break;
						case QuitSelected:
							System.exit(0);
							break;
						case AboutSelected:
							mainMenu.setMenuState(MenuState.AboutOpen);
							break;
						case AboutOpen:
							mainMenu.setMenuState(MenuState.AboutSelected);
							break;
						}
					}
					else if (!gameOver) {
						pauseGame();
						Sound.pause.play(playSounds);
					}
					else if(gameOver) {
						GameOverMenuState menuState = gameOverMenu.getMenuState();
						
						switch (menuState) {
						case PlayAgainSelected:
							stopGame();
							wcTop.startGame(period);
							break;
						case QuitSelected:
							System.exit(0);
							break;
						case AboutSelected:
							gameOverMenu.setMenuState(GameOverMenuState.AboutOpen);
							break;
						case AboutOpen:
							gameOverMenu.setMenuState(GameOverMenuState.AboutSelected);
							break;
						}
					}
				}
			}

		});

	}

	private void moveShapeListeners() {
		addKeyListener(new KeyAdapter() {

			// listen for arrows keys and space
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == KeyEvent.VK_SPACE) {
					spacePressed = true;
				}

				if (keyCode == KeyEvent.VK_R) {
					if (wcTop.getClass().getName() == "ui.GameMainWindow") {
						stopGame();
						wcTop.startGame(period);
					}
				}
				switch (keyCode) {

				case KeyEvent.VK_UP:
					rotateShapeLeft();

					if (isPaused) {
						mainMenu.handleMenuStateMovingUp();
					}
					else if(gameOver) {
						gameOverMenu.handleMenuStateMovingUp();
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!goStraightDown && !isPaused && !gameOver) {
						revertCanMoveAndGoStraightDown();
						Sound.godown.play(playSounds);
					}
					else if (isPaused) {
						mainMenu.handleMenuStateMovingDown();
					}
					else if(gameOver) {
						gameOverMenu.handleMenuStateMovingDown();
					}
					break;
				case KeyEvent.VK_LEFT:
					if (canMove)
						moveLeft();
					break;
				case KeyEvent.VK_RIGHT:
					if (canMove)
						moveRight();
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == KeyEvent.VK_SPACE) {
					spacePressed = false;
				}
			}

		});

	}

	private void rotateShapeLeft() {
		if (isPaused || gameOver)
			return;

		if (!currentShape.canRotateLeft(boardMatrix))
			return;

		makeCurrentShapeNull();

		currentShape.rotateLeft();

		for (int i = 0; i < currentShape.getBlocks().length; i++) {
			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();

			boardMatrix[x][y] = tempBlock;
		}

		Sound.rotate.play(playSounds);
	}

	private void moveRight() {

		if (!canMoveRight() || isPaused || gameOver)
			return;

		makeCurrentShapeNull();

		int increment = 1;
		updateCurrentShapeYPos(increment);

	}

	private void updateCurrentShapeYPos(int increment) {
		for (int i = 0; i < currentShape.getBlocks().length; i++) {
			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();

			boardMatrix[x][y + increment] = tempBlock;
			currentShape.setBlockPosition(x, y + increment, i);
		}
	}

	private void makeCurrentShapeNull() {
		for (int i = 0; i < currentShape.getBlocks().length; i++) {
			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();

			boardMatrix[x][y] = null;
		}
	}

	private boolean canMoveRight() {

		for (int i = 0; i < currentShape.getBlocks().length; i++) {

			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();
			if (tempBlock.getY() >= boardWidth - 1 || checkPositionAlreadyFilled(x, y + 1)) {
				return false;
			}
		}
		return true;
	}

	private void moveLeft() {

		if (!canMoveLeft() || isPaused || gameOver)
			return;

		makeCurrentShapeNull();

		int increment = -1;
		updateCurrentShapeYPos(increment);

	}

	private boolean canMoveLeft() {

		for (int i = 0; i < currentShape.getBlocks().length; i++) {

			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();
			if (tempBlock.getY() == 0 || checkPositionAlreadyFilled(x, y - 1)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkPositionAlreadyFilled(int x, int y) {
		return boardMatrix[x][y] != null && !currentShape.shapeHasPosition(x, y);
	}

	private void readyForTermination() {
		addKeyListener(new KeyAdapter() {
			// listen for esc, q, end, ctrl-c on the canvas to
			// allow a convenient exit from the full screen configuration
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					running = false;
					System.exit(0);
				}
			}
		});
	} // end of readyForTermination()

	public void addNotify()
	// wait for the JPanel to be added to the JFrame before starting
	{
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}

	public void startGame()
	// initialise and start the thread
	{
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // end of startGame()

	// ------------- game life cycle methods ------------
	// called by the JFrame's window listener methods

	public void resumeGame()
	// called when the JFrame is activated / deiconified
	{
		isPaused = false;
	}

	public void pauseGame()
	// called when the JFrame is deactivated / iconified
	{
		if (!gameOver) {
			isPaused = true;
		}
	}

	public void stopGame()
	// called when the JFrame is closing
	{
		running = false;
	}

	private void placeShapeInInicialPos() {

		int count = 0;
		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 4; k++) {

				if (currentShape.getPieceRepresentation()[i][k] != null) {

					if (boardMatrix[i][k + 2] != null) {
						gameOver = true;
					}
					boardMatrix[i][k + 2] = currentShape.getPieceRepresentation()[i][k];
					currentShape.setBlockPosition(i, k + 2, count);

					count++;
				}
			}
		}
	}

	public void run()
	/* The frames of the animation are drawn inside the while loop. */
	{
		long beforeTime, afterTime, timeDiff, sleepTime;
		int overSleepTime = 0;
		int noDelays = 0;
		int excess = 0;

		gameStartTime = System.currentTimeMillis();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;

		running = true;

		placeShapeInInicialPos();

		Sound.startGame.play(playSounds);

		while (running) {

			gameUpdate();
			gameRender(); // render the game to a buffer
			paintScreen(); // draw the buffer on-screen

			afterTime = System.currentTimeMillis();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime); // already in ms
				}
				catch (InterruptedException ex) {
				}
				overSleepTime = (int) ((System.currentTimeMillis() - afterTime) - sleepTime);
			}
			else { // sleepTime <= 0; the frame took longer than the
					// period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to
					noDelays = 0;
				}
			}

			beforeTime = System.currentTimeMillis();

			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate(); // update state but don't render
				skips++;
			}
			framesSkipped += skips;

			storeStats();
		}

		wcTop.remove(this);
	}

	private void gameUpdate() {

		if (!isPaused && !gameOver) {

			collapseMarkedLines();
			tryMoveCurrentShapeDown();

		}
	}

	private void collapseMarkedLines() {

		for (int i = 0; i < boardHeigth; i++) {

			if (boardMatrix[i][0] != null && boardMatrix[i][0].isCollapsed()) {
				long currentTime = System.currentTimeMillis();

				if (currentTime - lastUpdateTimeCollapse > 300) {

					collapseLine(i);
					moveRemainingBlocksDown(i);

					lastUpdateTimeCollapse = currentTime;

					Sound.collapse.play(playSounds);
				}
			}

		}
	}

	private void tryMoveCurrentShapeDown() {

		long currentTime = System.currentTimeMillis();

		// XXX lock delay
		calculateFrameTime();

		if (!canMoveDown()) {

			checkFullLines();
			// currentShape = new TShape();
			currentShape = selectRandomShape();

			if (!canMove) {
				revertCanMoveAndGoStraightDown();
			}

			placeShapeInInicialPos();
			return;
		}

		currentTime = System.currentTimeMillis();

		if (currentTime - lastUpdateTime > frameTime) {

			int shapesBlocksNumber = currentShape.getBlocks().length;

			makeCurrentShapeNull();

			for (int i = 0; i < shapesBlocksNumber; i++) {
				Block tempBlock = currentShape.getBlocks()[i];
				int x = tempBlock.getX();
				int y = tempBlock.getY();

				boardMatrix[x + 1][y] = tempBlock;
				currentShape.setBlockPosition(x + 1, y, i);
			}

			lastUpdateTime = currentTime + frameTime;
		}

	}

	private void calculateFrameTime() {

		if (spacePressed) {
			frameTime = 10;
		}
		else if (goStraightDown) {
			frameTime = -500;
		}
		else {
			switch (level) {
			case 0:
				frameTime = 300;
				break;
			case 1:
				frameTime = 280;
				break;
			case 2:
				frameTime = 260;
				break;
			case 3:
				frameTime = 240;
				break;
			case 4:
				frameTime = 220;
				break;
			case 5:
				frameTime = 200;
				break;
			case 6:
				frameTime = 180;
				break;
			case 7:
				frameTime = 160;
				break;
			case 8:
				frameTime = 140;
				break;
			case 9:
				frameTime = 120;
				break;
			case 10:
				frameTime = 100;
				break;
			case 11:
				frameTime = 80;
				break;
			case 12:
				frameTime = 70;
				break;
			case 13:
				frameTime = 60;
				break;
			case 14:
				frameTime = 50;
				break;
			case 15:
				frameTime = 40;
				break;
			case 16:
				frameTime = 30;
				break;
			case 17:
				frameTime = 20;
				break;
			default:
				frameTime = 10;
				break;
			}

		}

	}

	private void revertCanMoveAndGoStraightDown() {
		if (canMove) {
			goStraightDown = true;
			canMove = false;
		}
		else {
			goStraightDown = false;
			canMove = true;
		}
	}

	private void checkFullLines() {

		linesBeingCollapsed = 0;
		int shapesBlocksNumber = currentShape.getBlocks().length;

		for (int i = 0; i < shapesBlocksNumber; i++) {
			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();

			if (lineIsFull(x)) {
				markLineAsCollapsed(x);

				long currentTime = System.currentTimeMillis();
				lastUpdateTimeCollapse = currentTime;
			}
		}

		calculateScore();
		linesBeingCollapsed = 0;
	}

	private void calculateScore() {

		if (linesBeingCollapsed == 0) {
			return;
		}
		else {
			if (linesBeingCollapsed == 1) {
				score += 40 * (level + 1);
			}
			if (linesBeingCollapsed == 2) {
				score += 100 * (level + 1);
			}
			if (linesBeingCollapsed == 3) {
				score += 300 * (level + 1);
			}
			if (linesBeingCollapsed == 4) {
				score += 1200 * (level + 1);
			}

			level++;
		}

	}

	private void markLineAsCollapsed(int linePos) {

		if (boardMatrix[linePos][0].isCollapsed())
			return;

		linesBeingCollapsed += 1;

		for (int i = 0; i < boardWidth; i++) {
			boardMatrix[linePos][i].setCollapsed(true);
		}
	}

	private boolean lineIsFull(int linePos) {

		for (int i = 0; i < boardWidth; i++) {
			if (boardMatrix[linePos][i] == null)
				return false;
		}
		return true;
	}

	private void collapseLine(int linePos) {

		for (int i = 0; i < boardWidth; i++) {
			boardMatrix[linePos][i] = null;
		}
	}

	private void moveRemainingBlocksDown(int linePos) {

		for (int i = linePos - 1; i >= 0; i--) {
			for (int j = 0; j < boardWidth; j++) {

				if (boardMatrix[i][j] != null && !currentShape.shapeHasPosition(i, j)) {
					boardMatrix[i + 1][j] = boardMatrix[i][j];
					boardMatrix[i][j] = null;
				}
			}
		}
	}

	private Shape selectRandomShape() {

		Random rand = new Random();
		int randomNumber = rand.nextInt(7);

		switch (randomNumber) {
		case 0:
			return new IShape();
		case 1:
			return new JShape();
		case 2:
			return new LShape();
		case 3:
			return new OShape();
		case 4:
			return new SShape();
		case 5:
			return new TShape();
		case 6:
			return new ZShape();

		}
		return null;
	}

	private boolean canMoveDown() {

		for (int i = 0; i < currentShape.getBlocks().length; i++) {

			Block tempBlock = currentShape.getBlocks()[i];
			int x = tempBlock.getX();
			int y = tempBlock.getY();
			if (x >= boardHeigth - 1 || checkPositionAlreadyFilled(x + 1, y)) {
				return false;

			}
		}
		return true;
	}

	private void gameRender() {
		if (dbImage == null) {
			dbImage = createImage(windowWidth, windowHeigth);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			else
				boardGraphics = dbImage.getGraphics();
		}

		// clear the background
		if (bgImage == null) {
			boardGraphics.setColor(Color.black);
			boardGraphics.fillRect(0, 0, 800, 600);
		}
		else
			boardGraphics.drawImage(bgImage, 0, 0, this);

		boardGraphics.setFont(font);

		boardGraphics.setColor(Color.black);
		boardGraphics.drawRect(padding, 0, boardWidthInPixels, boardHeigthInPixels);

		drawMatrixLines();
		drawBoardMatrix();

		drawScoreBox();

		if (isPaused) {
			mainMenu.show(boardGraphics);
		}

		//gameOver = true;
		if (gameOver) {
			boardGraphics.setColor(new Color(0, 74, 127, 200));
			boardGraphics.fillRect(padding, 0, boardWidthInPixels + 1, boardHeigthInPixels + 1);
			
			boardGraphics.setColor(Color.white);
			gameOverMenu.show(boardGraphics);
			//drawGameOver();
		}

		// drawDebugInfo();
	}

	private void drawMatrixLines() {
		for (int i = 0; i < boardHeigth; i++) {

			boardGraphics.setColor(linesColor);
			boardGraphics.drawLine(padding, 0, padding + boardWidthInPixels, 0);
			boardGraphics.drawLine(padding, (i + 1) * blockWidth, padding + boardWidthInPixels, (i + 1) * blockWidth);
			for (int j = 0; j < boardWidth; j++) {

				boardGraphics.drawLine(padding, 0, padding, boardHeigthInPixels);
				boardGraphics.drawLine((j + 1) * blockWidth + padding, 0, (j + 1) * blockWidth + padding, boardHeigthInPixels);

			}
		}
	}

	private void drawGameOver() {
		boardGraphics.setColor(new Color(0, 74, 127, 200));
		boardGraphics.fillRect(padding, 0, boardWidthInPixels + 1, boardHeigthInPixels + 1);

		boardGraphics.setColor(Color.white);
		boardGraphics.setFont(font);
		int x = boardWidthInPixels / 2 - 30;
		int y = boardHeigthInPixels / 2 - 30;
		boardGraphics.drawString("Game Over", x + padding - 35, y);

		if (wcTop.getClass().getName() == "ui.GameMainWindow") {
			boardGraphics.drawString("Restart: (r)", x + padding - 70, y + 250);
			boardGraphics.drawString("Quit: (Esc)", x + padding - 70, y + 275);
		}

		drawGameOverScoreBox(x, y);
	}

	private void drawGameOverScoreBox(int x, int y) {
		boardGraphics.setColor(Color.white);

		int boxwidth = utils.getBoxWidth();
		int boxHeigth = utils.getBoxHeigth();

		int scoreBoxAlignment = padding - 65;

		Stroke oldStroke = guiUtils.changeDrawRectThickness(boardGraphics);
		boardGraphics.drawRect(x + scoreBoxAlignment - 5, y + 60, boxwidth, boxHeigth);
		((Graphics2D) boardGraphics).setStroke(oldStroke);

		boardGraphics.setFont(font);
		boardGraphics.drawString("Level: ", x + scoreBoxAlignment + 20, y + 95);
		boardGraphics.drawString(String.format("%02d", level) + "", x + scoreBoxAlignment + 110, y + 95);

		boardGraphics.drawString("Score: ", x + scoreBoxAlignment + 20, y + 115);
		boardGraphics.drawString(String.format("%05d", score) + "", x + scoreBoxAlignment + 110, y + 115);
	}

	private void drawScoreBox() {

		boardGraphics.setColor(linesColor);
		boardGraphics.setColor(Color.white);
		int boxwidth = utils.getBoxWidth();
		int boxHeigth = utils.getBoxHeigth();

		int xDraw = utils.getBoxX();
		int yDraw = utils.getBoxY();
		Stroke oldStroke = guiUtils.changeDrawRectThickness(boardGraphics);
		boardGraphics.drawRect(xDraw, yDraw, boxwidth, boxHeigth);
		((Graphics2D) boardGraphics).setStroke(oldStroke);

		boardGraphics.setColor(Color.white);
		boardGraphics.setFont(font);
		boardGraphics.drawString("Level: ", xDraw + 20, yDraw + 35);
		boardGraphics.drawString(String.format("%02d", level) + "", xDraw + 110, yDraw + 35);

		boardGraphics.drawString("Score: ", xDraw + 20, yDraw + 55);
		boardGraphics.drawString(String.format("%05d", score) + "", xDraw + 110, yDraw + 55);
	}

	private void drawDebugInfo() {
		boardGraphics.setColor(Color.white);
		boardGraphics.setFont(debugFont);

		boardGraphics.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " + df.format(averageUPS), 20, 25);
		boardGraphics.drawString("Window height/width: " + df.format(getSize().height) + " " + df.format(getSize().width), 20, 40);
		boardGraphics.drawString("Frame time: " + df.format(frameTime), 20, 55);
	}

	private void drawBoardMatrix() {
		for (int i = 0; i < boardHeigth; i++) {

			for (int j = 0; j < boardWidth; j++) {

				if (boardMatrix[i][j] != null) {

					Block currentBlock = boardMatrix[i][j];

					Color color = currentBlock.getBlockColor();
					boardGraphics.setColor(color);

					if (currentBlock.isCollapsed())
						boardGraphics.setColor(color.brighter());

					int blockWidth = currentBlock.getBlockWidth();
					boardGraphics.fill3DRect(j * blockWidth + padding, i * blockWidth, blockWidth, blockWidth, true);

				}
			}
		}
	}

	private void paintScreen()
	// use active rendering to put the buffered image on-screen
	{
		Graphics g;
		try {
			g = this.getGraphics();

			if ((g != null) && (dbImage != null)) {
				int middle = (windowWidth / 2) - boardWidthInPixels / 2;
				g.drawImage(dbImage, 0, 0, null);
			}

			Toolkit.getDefaultToolkit().sync(); // sync the display on
			// some systems
			g.dispose();
		}
		catch (Exception e) {
			System.out.println("Graphics error: " + e);
		}
	}

	private void storeStats()
	/*
	 * The statistics: - the summed periods for all the iterations in this
	 * interval (period is the amount of time a single frame iteration should
	 * take), the actual elapsed time in this interval, the error between these
	 * two numbers;
	 * 
	 * - the total frame count, which is the total number of calls to run();
	 * 
	 * - the frames skipped in this interval, the total number of frames
	 * skipped. A frame skip is a game update without a corresponding render;
	 * 
	 * - the FPS (frames/sec) and UPS (updates/sec) for this interval, the
	 * average FPS & UPS over the last NUM_FPSs intervals.
	 * 
	 * The data is collected every MAX_STATS_INTERVAL (1 sec).
	 */
	{
		frameCount++;
		statsInterval += period;

		if (statsInterval >= MAX_STATS_INTERVAL) { // record stats every
			// MAX_STATS_INTERVAL
			long timeNow = System.currentTimeMillis();
			timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000L); // ms
			// -->
			// secs

			long realElapsedTime = timeNow - prevStatsTime; // time since
			// last stats
			// collection
			totalElapsedTime += realElapsedTime;

			double timingError = ((double) (realElapsedTime - statsInterval) / statsInterval) * 100.0;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * 1000L);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000L);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			}
			else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}

			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	} // end of storeStats()

}