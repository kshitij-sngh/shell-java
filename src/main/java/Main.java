import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;

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
                String commandName = arguments[0];
                CommandAction action = CommandRegistry.getCommandAction(commandName);

                if(action !=null)
                {
                    String result = action.execute(arguments, out, err, currentDir);
                    if(result.equals(Constants.SHUTDOWN_SIGNAL))
                        break;
                    currentDir = result;
                }
                else
                {
                    String subCmd = arguments[0];
                    Path path = Helper.checkPathForCmd(subCmd);
                    if (path == null)
                        err.println(commandName + ": command not found");
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
