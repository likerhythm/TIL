package org.example.springtest;

import org.junit.jupiter.api.Test;

public class PostConstructTest {

    @org.junit.jupiter.api.Test
    public void test() {
        Test t = new Test();
    }

    static class Test {

        public void post() {
            System.out.println("Test.post");
        }

        public void destroy() {
            System.out.println("Test.destroy");
        }
    }
}
