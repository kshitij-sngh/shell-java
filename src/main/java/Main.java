import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader inpReader = new BufferedReader(new InputStreamReader(System.in));
        String currentDir = System.getProperty("user.dir"); //"/tmp/pineapple";

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
                    if(arguments.length>1) {
                        String cdArg = arguments[1];
                        Path targetPath;
                        if(cdArg.startsWith("~"))
                        {
                            String envHome = System.getenv("home");
                            if(cdArg.length()>1)
                                targetPath = Path.of(envHome, cdArg.substring(1)).resolve(cdArg).normalize();
                            else
                                targetPath = Path.of(envHome);


                        }
                        else
                        {
                            targetPath = Path.of(currentDir).resolve(cdArg).normalize();
                        }

                        if(Files.isDirectory(targetPath))
                        {
                            currentDir = targetPath.toAbsolutePath().toString();
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
                        pb.directory(new File(currentDir));
                        pb.inheritIO();
                        Process process = pb.start();
                        process.waitFor();
                    }
            }
        }
    }
}
