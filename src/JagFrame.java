// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import idlers.GameViewport;
import idlers.WindowSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.file.Path;

// IdleRS: the window is resizable and the game screen is scaled to fill it (see idlers.GameViewport). Every draw goes
// through getGraphics(), so scaling that Graphics scales the whole client; mouse events are mapped back by the applet.
@SuppressWarnings("serial")
public class JagFrame extends Frame {

	public JagFrame(int width, int height, JagApplet _applet) {
		applet = _applet;
		gameWidth = width;
		gameHeight = height;
		setTitle("Jagex");
		setBackground(Color.black);
		addNotify();
		Insets insets = getInsets();
		int borderWidth = insets.left + insets.right;
		int borderHeight = insets.top + insets.bottom;
		WindowSize size = initialSize(borderWidth, borderHeight);
		setMinimumSize(new Dimension(width + borderWidth, height + borderHeight));
		setSize(size.width(), size.height());
		viewport = fitViewport();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				refreshViewport();
				new WindowSize(getWidth(), getHeight()).write(SIZE_FILE); // IdleRS: reopened at this size
			}
		});
		setVisible(true);
		toFront();
	}

	public int toGameX(int windowX) {
		return viewport.toGameX(windowX);
	}

	public int toGameY(int windowY) {
		return viewport.toGameY(windowY);
	}

	@Override
	public Graphics getGraphics() {
		Graphics2D g = (Graphics2D) super.getGraphics();
		if (g == null)
			return null;
		GameViewport current = viewport;
		g.translate(current.offsetX(), current.offsetY());
		g.scale(current.scale(), current.scale());
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		return g;
	}

	@Override
	public void update(Graphics g) {
		refreshViewport();
		applet.update(g);
	}

	@Override
	public void paint(Graphics g) {
		refreshViewport();
		applet.paint(g);
	}

	private WindowSize initialSize(int borderWidth, int borderHeight) {
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		WindowSize saved = WindowSize.read(SIZE_FILE).orElse(null);
		try {
			return WindowSize.initial(System.getenv(GameViewport.SCALE_VARIABLE), saved, gameWidth, gameHeight,
					borderWidth, borderHeight, screen.width, screen.height);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage() + ", opening at the auto scale instead");
			return WindowSize.initial("auto", null, gameWidth, gameHeight, borderWidth, borderHeight, screen.width, screen.height);
		}
	}

	private GameViewport fitViewport() {
		Insets insets = getInsets();
		return GameViewport.fit(gameWidth, gameHeight, insets.left, insets.top,
				getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);
	}

	// Also called from paint: the real window borders can arrive after the window is shown, without a resize event.
	private void refreshViewport() {
		GameViewport fitted = fitViewport();
		if (fitted.equals(viewport))
			return;
		viewport = fitted;
		Graphics raw = super.getGraphics();
		if (raw != null) {
			raw.setColor(Color.black);
			raw.fillRect(0, 0, getWidth(), getHeight());
			raw.dispose();
		}
		applet.graphics = getGraphics();
		applet.update(applet.graphics);
	}

	private static final Path SIZE_FILE = Path.of(WindowSize.FILE);

	public JagApplet applet;
	private final int gameWidth;
	private final int gameHeight;
	private volatile GameViewport viewport;
}
