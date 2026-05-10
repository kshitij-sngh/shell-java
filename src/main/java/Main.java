import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            //String[] arguments = line.trim().split(" ");
            String[] arguments = Helper.parseArguments(line);
            String command = arguments[0];

            PrintStream out = System.out;
            boolean isOutputRedirectedToFile = false;
            String outputFilePath = "";
            File outputFile = null;

            PrintStream err = System.err;
            boolean isErrRedirectedToFile = false;
            String errFilePath = "";
            File errFile = null;

            List<String> tmp = new ArrayList<>();
            for(int i=0; i<arguments.length; i++)
            {    if((">".equals(arguments[i]) || "1>".equals(arguments[i])) && i+1<arguments.length) {
                    isOutputRedirectedToFile = true;
                    outputFilePath = arguments[i+1];
                    i++;
                }
                else if("2>".equals(arguments[i]) && i+1<arguments.length) {
                    isErrRedirectedToFile = true;
                    errFilePath = arguments[i+1];
                    i++;
                }
                else
                    tmp.add(arguments[i]);
            }
            arguments = tmp.toArray(new String[0]);
            if(isOutputRedirectedToFile)
            {
                outputFile = new File(currentDir).toPath().resolve(outputFilePath).toFile();
                out = new PrintStream(outputFile);
            }
            if(isErrRedirectedToFile)
            {
                errFile = new File(currentDir).toPath().resolve(errFilePath).toFile();
                err = new PrintStream(errFile);
            }

            String subCmd;
            switch (command)
            {
                case "exit":
                    return;
                case "echo":
                    if(arguments.length>1) {
                        out.print(arguments[1]);
                        for (int i = 2; i < arguments.length; i++)
                            out.print(" " + arguments[i]);
                    }
                    out.println();
                    break;
                case "type":
                    subCmd = arguments[1];
                    if(Constants.BUILT_IN_CMDS.contains(subCmd))
                        out.println(subCmd+" is a shell builtin");
                    else {
                        Path path = Helper.checkPathForCmd(subCmd);
                        if(path!=null)
                            out.println(subCmd + " is "+path);
                        else
                            err.println(subCmd + ": not found");
                    }
                    break;
                case "pwd":
                    out.println(currentDir);
                    break;
                case "cd":
                    if(arguments.length>1) {
                        String cdArg = arguments[1];
                        Path targetPath;
                        if(cdArg.startsWith("~"))
                        {
                            String envHome = System.getenv("HOME");
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
                            err.println("cd: "+cdArg+": No such file or directory");

                    }
                    break;
                default:
                    subCmd = arguments[0];
                    Path path = Helper.checkPathForCmd(subCmd);
                    if(path==null)
                        err.println(command + ": command not found");
                    else
                    {
                        ProcessBuilder pb = new ProcessBuilder(arguments);
                        pb.directory(new File(currentDir));

                        if(isOutputRedirectedToFile)
                        {
                            pb.redirectOutput(ProcessBuilder.Redirect.to(outputFile));
                        }
                        else
                            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

                        if(isErrRedirectedToFile)
                        {
                            pb.redirectError(ProcessBuilder.Redirect.to(errFile));
                        }
                        else
                            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                        Process process = pb.start();
                        process.waitFor();
                    }
            }

            if(isOutputRedirectedToFile && out != System.out)
                out.close();
            if(isErrRedirectedToFile && err != System.err)
                err.close();
        }
    }
}
