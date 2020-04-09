package org.baseagent.ui;

import javafx.scene.paint.Color;

public class BaseAgentGradient {
	private Color color1;
	private Color color2;
	private double minVal;
	private double maxVal;
	
	public BaseAgentGradient(Color color1, Color color2, double minVal, double maxVal) {
		this.color1 = color1;
		this.color2 = color2;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
	public Color getColorFor(double value) {
		double percent = value / (maxVal - minVal);
		double red = color1.getRed() + percent * (color2.getRed() - color1.getRed());
		double green = color1.getGreen() + percent * (color2.getGreen() - color1.getGreen());
		double blue = color1.getBlue() + percent * (color2.getBlue() - color1.getBlue());
		double alpha = color1.getOpacity() + percent * (color2.getOpacity() - color1.getOpacity());
		
		if (red > 1.0) red = 1.0; if (red < 0.0) red = 0.0;
		if (green > 1.0) green = 1.0; if (green < 0.0) green = 0.0;
		if (blue > 1.0) blue = 1.0; if (blue < 0.0) blue = 0.0;
		if (alpha > 1.0) alpha = 1.0; if (alpha < 0.0) alpha = 0.0;
		
		return new Color(red, green, blue, alpha);
	}
}
