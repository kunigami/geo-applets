package me.kuniga.compgeo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Stroke;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author Guilherme Kunigami
 */
public class AppletEngine extends java.applet.Applet {

	private static final long serialVersionUID = 4742814160765188925L;
	/* Indica o estágio do algoritmo em que estamos */
	int stage;
	/* Decide se alguma mudança foi feita */
	Boolean steped;
	/* Decide se está na execução automática */
	Boolean auto;

	/* Temporizador */
	Timer timer;

	/* Define estilos de linha */
	Stroke bold = new BasicStroke(2);
	Stroke standard = new BasicStroke(1);
	// Linha pontilhada
	Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

	/* Método para execução automática */
	public void AutomaticExecution(int period) {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				steped = true;
				repaint();
				if (auto == false)
					timer.cancel();
			}
		};
		timer.scheduleAtFixedRate(task, 0, period);
	}

	/*
	 * Otimiza a renderização pintando tudo como se fosse uma imagem em
	 * background.
	 */
	Dimension offDimension;
	Image offImage;
	Graphics offGraphics;

	public void update(Graphics g) {

		Dimension d = getSize();

		// Cria os gráficos offscreen
		if ((offGraphics == null) || (d.width != offDimension.width)
				|| (d.height != offDimension.height)) {
			offDimension = d;
			offImage = createImage(d.width, d.height);
			offGraphics = offImage.getGraphics();
		}

		// Apaga a imagem anterior
		offGraphics.setColor(getBackground());
		offGraphics.fillRect(0, 0, d.width, d.height);
		offGraphics.setColor(Color.black);

		// Pinta o frame como uma imagem
		paint(offGraphics);

		// Pinta a imagem na tela
		g.drawImage(offImage, 0, 0, null);
	}

}
