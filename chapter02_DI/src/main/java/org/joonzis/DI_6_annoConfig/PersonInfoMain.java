package org.joonzis.DI_6_annoConfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PersonInfoMain {
	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext ctx = 
				new AnnotationConfigApplicationContext(AnnoConfig.class);
		
		Person person1 = (Person)ctx.getBean("human1");
		System.out.println(person1.getName());
		System.out.println(person1.getAge());
		System.out.println(person1.getHobbies());
		
	}
}
