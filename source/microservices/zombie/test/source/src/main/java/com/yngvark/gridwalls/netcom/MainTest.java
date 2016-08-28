package com.yngvark.gridwalls.netcom;

import java.util.Scanner;

public class MainTest {
    public static void main(String[] args) {
        new MainTest().arne2();
    }

    public void arne() {
        Scanner s = new Scanner("Enter something:");
        checkNext(s);
    }

    public void arne2() {
        Scanner s = new Scanner(System.in);
        checkNext(s);
    }

    private void checkNext(Scanner s) {
        boolean hasNext = s.hasNext("Enter something:");
        System.out.println("hanext: " + hasNext);
        String next = s.next("Enter something:");
        System.out.println("hanext: " + next);
    }
}
