import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print("$ ");

        byte[] input = System.in.readAllBytes();
        String command = Arrays.toString(input);
        System.out.print("{"+command+"}: command not found");
    }
}
