package gameModel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	public static Sound rotate = loadSound("/sounds/rotate2.wav");
	public static Sound godown = loadSound("/sounds/godown5.wav");
	public static Sound pause = loadSound("/sounds/pause4.wav");
	public static Sound collapse = loadSound("/sounds/collapse4.wav");
	public static Sound startGame = loadSound("/sounds/startgame.wav");
	public static Sound menuMove = loadSound("/sounds/menu_move.wav");
	
	public static Sound loadSound(String fileName) {
		Sound sound = new Sound();
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getResource(fileName));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			sound.clip = clip;
		} catch (Exception e) {
			System.out.println(e);
		}
		return sound;
	}

	private Clip clip;

	public void play(boolean playSound) {
		
		if (!playSound) {
			return;
		}
		
		try {
			if (clip != null) {
				new Thread() {
					public void run() {
						synchronized (clip) {
							clip.stop();
							clip.setFramePosition(0);
							clip.start();
						}
					}
				}.start();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}