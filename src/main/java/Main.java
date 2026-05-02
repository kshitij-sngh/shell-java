import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print("$ ");
        BufferedReader inpReader = new BufferedReader(new InputStreamReader(System.in));
        String line = inpReader.readLine();
        System.out.print(line+": command not found");
    }
}
