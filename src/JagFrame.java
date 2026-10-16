// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import idlers.GameViewport;
import idlers.WindowSize;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.nio.file.Path;

@SuppressWarnings("serial")
public class JagFrame extends Frame {

	public JagApplet applet;

	public JagFrame(int width, int height, JagApplet applet, boolean resizable) {
		this.applet = applet;
		setTitle("Jagex");
		setBackground(Color.black); // IdleRS: the bars around the scaled fixed mode
		setResizable(true); // IdleRS: the fixed mode scales the game to fill the window, so it resizes too
		// create the native peer first so the real window insets are known
		addNotify();
		if (resizable)
			setContentSize(width, height);
		else
			setFixedModeSize(width, height);
		setVisible(true);
		toFront();
	}

	/**
	 * Sizes the window so that the drawable area (inside the borders and title bar) is width x height.
	 */
	public void setContentSize(int width, int height) {
		Insets insets = getInsets();
		setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
	}

	/**
	 * IdleRS: sizes the window for the fixed mode, whose game of {@code gameWidth} x {@code gameHeight} is scaled to fill
	 * it: the size it was last left at, unless IDLERS_CLIENT_SCALE sets one (see idlers.WindowSize).
	 */
	public void setFixedModeSize(int gameWidth, int gameHeight) {
		Insets insets = getInsets();
		int borderWidth = insets.left + insets.right;
		int borderHeight = insets.top + insets.bottom;
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		WindowSize saved = WindowSize.read(FIXED_MODE_SIZE_FILE).orElse(null);
		WindowSize size;
		try {
			size = WindowSize.initial(System.getenv(GameViewport.SCALE_VARIABLE), saved, gameWidth, gameHeight,
					borderWidth, borderHeight, screen.width, screen.height);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage() + ", opening at the auto scale instead");
			size = WindowSize.initial("auto", null, gameWidth, gameHeight, borderWidth, borderHeight, screen.width, screen.height);
		}
		setSize(size.width(), size.height());
	}

	/** IdleRS: keeps the window's size for the next time the fixed mode opens. */
	public void saveFixedModeSize() {
		new WindowSize(getWidth(), getHeight()).write(FIXED_MODE_SIZE_FILE);
	}

	/**
	 * Sets the smallest allowed drawable area (inside the borders and title bar).
	 */
	public void setMinimumContentSize(int width, int height) {
		Insets insets = getInsets();
		setMinimumSize(new java.awt.Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom));
	}

	public int getContentWidth() {
		Insets insets = getInsets();
		return getWidth() - insets.left - insets.right;
	}

	public int getContentHeight() {
		Insets insets = getInsets();
		return getHeight() - insets.top - insets.bottom;
	}

	@Override
	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
		if (g == null) {
			return null;
		}
		Insets insets = getInsets();
		g.translate(insets.left, insets.top);
		g.setClip(0, 0, getContentWidth(), getContentHeight());
		return g;
	}

	@Override
	public void update(Graphics g) {
		applet.update(g);
	}

	@Override
	public void paint(Graphics g) {
		applet.paint(g);
	}

	private static final Path FIXED_MODE_SIZE_FILE = Path.of(WindowSize.FILE);
}
