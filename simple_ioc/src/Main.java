import bean.Person;
import ioc.SimpleIOC;

public class Main {
    public static void main(String[] args) throws Exception {
        String file = SimpleIOC.class.getClassLoader().getResource("beans.xml").getFile();
        SimpleIOC simpleIOC = new SimpleIOC(file);
        Person person = (Person)simpleIOC.getBean("person");

        System.out.println(person.getName());
    }
}
