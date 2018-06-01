package test;

import java.util.List;

public class MyBean2 {

	private String name;
	private int age;
	private List<MyBean3> list;

	public List<MyBean3> getList() {
		return list;
	}

	public void setList(List<MyBean3> list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
