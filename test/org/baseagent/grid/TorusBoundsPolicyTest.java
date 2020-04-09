package org.baseagent.grid;

public class TorusBoundsPolicyTest {
	private TorusBoundsPolicy sut;
	
	public TorusBoundsPolicyTest() {
		this.sut = new TorusBoundsPolicy(10, 10);
	}
	
	public void testBoundX() {
		if (sut.boundX(0) != 0) System.out.println("Fail x1");
		if (sut.boundX(5) != 5) System.out.println("Fail x2");
		if (sut.boundX(-1) != 9) System.out.println("Fail x3 " + sut.boundX(-1));
		if (sut.boundX(-2) != 8) System.out.println("Fail x4 " + sut.boundX(-2));
		if (sut.boundX(-11) != 9) System.out.println("Fail x3n " + sut.boundX(-11));
		if (sut.boundX(-12) != 8) System.out.println("Fail x4n " + sut.boundX(-12));
		if (sut.boundX(10) != 0) System.out.println("Fail x5");
		if (sut.boundX(11) != 1) System.out.println("Fail x6");
		if (sut.boundX(20) != 0) System.out.println("Fail x5n");
		if (sut.boundX(20) != 0) System.out.println("Fail x6n");
	}

	public void testBoundY() {
		if (sut.boundY(0) != 0) System.out.println("Fail y1");
		if (sut.boundY(5) != 5) System.out.println("Fail y2");
		if (sut.boundY(-1) != 9) System.out.println("Fail y3");
		if (sut.boundY(-2) != 8) System.out.println("Fail y4");
		if (sut.boundY(10) != 0) System.out.println("Fail y5");
		if (sut.boundY(11) != 1) System.out.println("Fail y6");
	}

	public static void main(String[] args) {
		TorusBoundsPolicyTest test = new TorusBoundsPolicyTest();
		test.testBoundX();
		test.testBoundY();
	}
}
