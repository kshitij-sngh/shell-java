import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader inpReader = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            System.out.print("$ ");

            String line = inpReader.readLine();
            if(line==null)
                break;

            String[] arguments = line.trim().split(" ");
            String cmd = arguments[0];
            switch (cmd)
            {
                case "exit":
                    return;
                case "echo":
                    System.out.println(arguments[1]);
                    for(int i=2; i<arguments.length; i++)
                        System.out.print(" "+arguments[i]);
                    System.out.println();
                    break;
                default:
                    System.out.println(cmd + ": command not found");
            }
        }
    }
}
