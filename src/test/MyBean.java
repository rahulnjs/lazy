package test;

import java.util.List;

public class MyBean {

	private boolean bl;
	private int i;
	private float f;
	private double d;
	private String str;
	private long l;
	private MyBean2 b2;
	private List<MyBean2> b2List;
	private char c;
	private Integer in = 50;
	private int[] boom = new int[] { 1, 3, 5, 7, 9, 11 };

	public char getC() {
		return c;
	}

	public void setC(char c) {
		this.c = c;
	}

	public boolean isBl() {
		return bl;
	}

	public void setBl(boolean bl) {
		this.bl = bl;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public float getF() {
		return f;
	}

	public void setF(float f) {
		this.f = f;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public long getL() {
		return l;
	}

	public void setL(long l) {
		this.l = l;
	}

	public MyBean2 getB2() {
		return b2;
	}

	public void setB2(MyBean2 b2) {
		this.b2 = b2;
	}

	public List<MyBean2> getB2List() {
		return b2List;
	}

	public void setB2List(List<MyBean2> b2List) {
		this.b2List = b2List;
	}

	public Integer getIn() {
		return in;
	}

	public void setIn(Integer in) {
		this.in = in;
	}

	public int[] getBoom() {
		return boom;
	}

	public void setBoom(int[] boom) {
		this.boom = boom;
	}

}
