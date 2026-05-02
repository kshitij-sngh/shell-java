import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader inpReader = new BufferedReader(new InputStreamReader(System.in));
        String currentDir = System.getProperty("user.dir");
        while (true)
        {
            System.out.print("$ ");

            String line = inpReader.readLine();
            if(line==null)
                break;

            String[] arguments = line.trim().split(" ");
            String command = arguments[0];
            String subCmd;
            switch (command)
            {
                case "exit":
                    return;
                case "echo":
                    if(arguments.length>1) {
                        System.out.print(arguments[1]);
                        for (int i = 2; i < arguments.length; i++)
                            System.out.print(" " + arguments[i]);
                    }
                    System.out.println();
                    break;
                case "type":
                    subCmd = arguments[1];
                    if(Constants.BUILT_IN_CMDS.contains(subCmd))
                        System.out.println(subCmd+" is a shell builtin");
                    else {
                        Path path = Helper.checkPathForCmd(subCmd);
                        if(path!=null)
                            System.out.println(subCmd + " is "+path);
                        else
                            System.out.println(subCmd + ": not found");
                    }
                    break;
                case "pwd":
                    System.out.println(currentDir);
                    break;
                case "cd":
                    String cdArg = arguments[0];
                    if(cdArg.startsWith("/"))
                    {
                        Path path = Paths.get(cdArg);
                        if(Files.isDirectory(path))
                        {
                            currentDir = cdArg;
                        }
                        else
                            System.out.println("cd: "+cdArg+": No such file or directory");
                    }
                    break;
                default:
                    subCmd = arguments[0];
                    Path path = Helper.checkPathForCmd(subCmd);
                    if(path==null)
                        System.out.println(command + ": command not found");
                    else
                    {
                        ProcessBuilder pb = new ProcessBuilder(arguments);
                        pb.inheritIO();
                        Process process = pb.start();
                        process.waitFor();
                    }
            }
        }
    }
}
