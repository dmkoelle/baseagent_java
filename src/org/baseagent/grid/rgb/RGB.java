package org.baseagent.grid.rgb;

import java.awt.image.ColorModel;

public class RGB {
	public int a;
	public int r;
	public int g;
	public int b;

	public RGB(int r, int g, int b) {
		this(0, r, g, b);
	}

	public RGB(int a, int r, int g, int b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public static RGB get(int pixel, ColorModel colorModel) {
		return new RGB(colorModel.getAlpha(pixel), colorModel.getRed(pixel), colorModel.getGreen(pixel), colorModel.getBlue(pixel));
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o == null) || (!(o instanceof RGB))) {
			return false;
		}
		
		RGB o2 = (RGB)o;
		return ((a==o2.a)&&(r==o2.r)&&(g==o2.g)&&(b==o2.b)); 
	}
	
	@Override
	public int hashCode() {
		return a*37+r*41+g*71+b*23;
	}
}
