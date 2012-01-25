package utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SolratricUtils
{
	private Properties prop;

	public SolratricUtils() {
		prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream("/config/config.properties"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getSoundProperties() {

		return Boolean.parseBoolean(prop.getProperty("enable.sound"));
	}

	public int getBoardWidth() {

		return Integer.parseInt(prop.getProperty("board.width"));
	}

	public int getBoardHeigth() {

		return Integer.parseInt(prop.getProperty("board.heigth"));
	}

	public int getBlockWidth() {

		return Integer.parseInt(prop.getProperty("block.width"));
	}

	public int getWindowWidth() {

		return Integer.parseInt(prop.getProperty("window.width"));
	}

	public int getWindowHeigth() {

		return Integer.parseInt(prop.getProperty("window.heigth"));
	}

	public int getBoxHeigth() {

		return Integer.parseInt(prop.getProperty("box.heigth"));
	}

	public int getBoxWidth() {

		return Integer.parseInt(prop.getProperty("box.width"));
	}

	public int getBoxX() {
		return Integer.parseInt(prop.getProperty("box.x"));
	}

	public int getBoxY() {
		return Integer.parseInt(prop.getProperty("box.y"));
	}

	private final Font SERIF_FONT = new Font("serif", Font.PLAIN, 24);

	public Font getFont(String fontName) {
		Font font = null;
		try {
			InputStream fontFile = this.getClass().getResourceAsStream("/fonts/" + fontName);
			font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
		}
		catch (Exception ex) {
			font = SERIF_FONT;
			ex.printStackTrace();
		}
		return font;
	}

}
