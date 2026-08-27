package org.joonzis.DI_6_annoConfig;

public class PersonInfo {
	private Person person;
	
	public PersonInfo() {}
	
	public void info() {
		System.out.println("이름 : " + person.getName());
		System.out.println("이름 : " + person.getAge());
		System.out.println("이름 : " + person.getHobbies());
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	
}
