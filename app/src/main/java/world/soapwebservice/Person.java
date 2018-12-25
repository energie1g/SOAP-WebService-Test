package world.soapwebservice;

import org.ksoap2.serialization.SoapObject;
public class Person {
    private String name;
    private int age;

    // constructors
    public Person() {
        this.name = "na"; this.age = -1;
    }

    public Person(String name, int age) {
        this.name = name; this.age = age;
    }

    // create a local Java Person object using the KSOAP response (C#) object
    public Person(SoapObject obj) {
        this.name = obj.getProperty("personName").toString();
        this.age = Integer.parseInt(obj.getProperty("personAge").toString());
    }

    // accessors (get/set) ommitted for brevity...
    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + "]";
    }
}
