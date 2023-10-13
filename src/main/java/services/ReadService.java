package services;

import java.util.Scanner;

public class ReadService {
    private static Scanner sc = new Scanner(System.in);

    public static int readInt() {
        return Integer.parseInt(sc.next());
    }

    public static String readNextLine() {
        return sc.nextLine();
    }
}
