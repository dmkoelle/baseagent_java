package org.baseagent.grid;

public class TorusBoundsPolicyTest {
	private TorusBoundsPolicy torusBoundsPolicy;
	
	public TorusBoundsPolicyTest() {
		this.torusBoundsPolicy = new TorusBoundsPolicy(10, 10);
	}
	
	public void testBoundX() {
		if (torusBoundsPolicy.boundX(0) != 0) System.out.println("Fail x1");
		if (torusBoundsPolicy.boundX(5) != 5) System.out.println("Fail x2");
		if (torusBoundsPolicy.boundX(-1) != 9) System.out.println("Fail x3 " + torusBoundsPolicy.boundX(-1));
		if (torusBoundsPolicy.boundX(-2) != 8) System.out.println("Fail x4 " + torusBoundsPolicy.boundX(-2));
		if (torusBoundsPolicy.boundX(-11) != 9) System.out.println("Fail x3n " + torusBoundsPolicy.boundX(-11));
		if (torusBoundsPolicy.boundX(-12) != 8) System.out.println("Fail x4n " + torusBoundsPolicy.boundX(-12));
		if (torusBoundsPolicy.boundX(10) != 0) System.out.println("Fail x5");
		if (torusBoundsPolicy.boundX(11) != 1) System.out.println("Fail x6");
		if (torusBoundsPolicy.boundX(20) != 0) System.out.println("Fail x5n");
		if (torusBoundsPolicy.boundX(20) != 0) System.out.println("Fail x6n");
	}

	public void testBoundY() {
		if (torusBoundsPolicy.boundY(0) != 0) System.out.println("Fail y1");
		if (torusBoundsPolicy.boundY(5) != 5) System.out.println("Fail y2");
		if (torusBoundsPolicy.boundY(-1) != 9) System.out.println("Fail y3");
		if (torusBoundsPolicy.boundY(-2) != 8) System.out.println("Fail y4");
		if (torusBoundsPolicy.boundY(10) != 0) System.out.println("Fail y5");
		if (torusBoundsPolicy.boundY(11) != 1) System.out.println("Fail y6");
	}

	public static void main(String[] args) {
		TorusBoundsPolicyTest test = new TorusBoundsPolicyTest();
		test.testBoundX();
		test.testBoundY();
	}
}
