import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader inpReader = new BufferedReader(new InputStreamReader(System.in));
        String currentDir = System.getProperty("user.dir"); //"/tmp/pineapple";

        while (true) {
            System.out.print("$ ");

            String line = inpReader.readLine();
            if (line == null)
                break;

            //String[] arguments = line.trim().split(" ");
            String[] rawArguments = Helper.parseArguments(line);
            RedirectResult redirectResult = Helper.parseRedirection(rawArguments, currentDir);
            String[] arguments = redirectResult.getCleanedArgs();


            PrintStream out = Helper.getOutStream(redirectResult);
            PrintStream err = Helper.getErrStream(redirectResult);

            try{
                String command = arguments[0];
                String subCmd;

                switch (command) {
                    case "exit":
                        return;
                    case "echo":
                        if (arguments.length > 1) {
                            out.print(arguments[1]);
                            for (int i = 2; i < arguments.length; i++)
                                out.print(" " + arguments[i]);
                        }
                        out.println();
                        break;
                    case "type":
                        if (arguments.length >= 2) {
                            subCmd = arguments[1];
                            if (Constants.BUILT_IN_CMDS.contains(subCmd))
                                out.println(subCmd + " is a shell builtin");
                            else {
                                Path path = Helper.checkPathForCmd(subCmd);
                                if (path != null)
                                    out.println(subCmd + " is " + path);
                                else
                                    err.println(subCmd + ": not found");
                            }
                        }
                        break;
                    case "pwd":
                        out.println(currentDir);
                        break;
                    case "cd":
                        if (arguments.length > 1) {
                            String cdArg = arguments[1];
                            Path targetPath;
                            if (cdArg.startsWith("~")) {
                                String envHome = System.getenv("HOME");
                                if (cdArg.length() > 1)
                                    targetPath = Path.of(envHome, cdArg.substring(1)).resolve(cdArg).normalize();
                                else
                                    targetPath = Path.of(envHome);

                            } else {
                                targetPath = Path.of(currentDir).resolve(cdArg).normalize();
                            }

                            if (Files.isDirectory(targetPath)) {
                                currentDir = targetPath.toAbsolutePath().toString();
                            } else
                                err.println("cd: " + cdArg + ": No such file or directory");

                        }
                        break;
                    default:
                        subCmd = arguments[0];
                        Path path = Helper.checkPathForCmd(subCmd);
                        if (path == null)
                            err.println(command + ": command not found");
                        else {
                            ProcessBuilder pb = new ProcessBuilder(arguments);
                            pb.directory(new File(currentDir));

                            if (redirectResult.isAppendOutput())
                                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(redirectResult.getOutputFile()));
                            else if (redirectResult.getOutputFile()!=null) {
                                pb.redirectOutput(ProcessBuilder.Redirect.to(redirectResult.getOutputFile()));
                            } else
                                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

                            if (redirectResult.isAppendErr())
                                pb.redirectError(ProcessBuilder.Redirect.appendTo(redirectResult.getErrFile()));
                            else if (redirectResult.getErrFile() !=null) {
                                pb.redirectError(ProcessBuilder.Redirect.to(redirectResult.getErrFile()));
                            } else
                                pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                            Process process = pb.start();
                            process.waitFor();
                        }
                }
            }
            finally {
                if(out!=System.out)
                    out.close();
                if(err!=System.err)
                    err.close();
            }
        }
    }
}
