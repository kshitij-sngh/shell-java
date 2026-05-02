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
            System.out.flush();

            String line = inpReader.readLine();
            if(line==null)
                break;
            if("exit".equals(line))
                return;
            System.out.println(line + ": command not found");
        }
    }
}
