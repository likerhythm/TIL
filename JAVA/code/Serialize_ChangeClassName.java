package JAVA.code;

import java.io.*;

public class Serialize_ChangeClassName {

    static class Human { // 직렬화를 하기 위한 클래스
        // private static final long serialVersionUID = 1L; // 직렬화 버전 관리
        private String name;
        private int age;

        public Human(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public void displayInfo() {
            System.out.println("이름: " + name + ", 나이: " + age);
        }
    }
    public static void main(String[] args) {
        Human person = new Human("홍길동", 30);

         // 직렬화하여 파일에 저장
//         try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("person.ser"))) {
//             oos.writeObject(person);
//             System.out.println("객체가 person.ser 파일에 저장됨.");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

         // 역직렬화하여 객체 복원
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("person.ser"))) {
            Human deserializedPerson = (Human) ois.readObject();
            System.out.println("파일에서 객체를 읽어옴:");
            deserializedPerson.displayInfo();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
