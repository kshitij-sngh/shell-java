import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Helper {
    public static Path checkPathForCmd(String cmd)
    {
        String pathEnv = System.getenv("PATH");
        String[] directories = pathEnv.split(":");
        for (String directory: directories) {
            Path path = Paths.get(directory, cmd);
            if(Files.isExecutable(path))
                return path;
        }
        return null;
    }
    public static String[] parseArguments(String line)
    {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean insideSingleQuotes = false;
        boolean insideDoubleQuotes = false;
        char ch;

        for(int i=0; i<line.length(); i++)
        {
            ch = line.charAt(i);
            if('\\'==ch && !insideSingleQuotes)
            {
                if(i+1<line.length())
                    currentArg.append(line.charAt(i+1));
                i++;
            }
            else if('\"'==ch && !insideSingleQuotes)
            {
                insideDoubleQuotes=!insideDoubleQuotes;
            }
            else if('\''==ch && !insideDoubleQuotes)
            {
                insideSingleQuotes=!insideSingleQuotes;
            }
            else if(' '==ch && !insideDoubleQuotes && !insideSingleQuotes)
            {
                if(!currentArg.isEmpty()) {
                    args.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            }
            else
                currentArg.append(ch);
        }
        if(!currentArg.isEmpty())
            args.add(currentArg.toString());

        return args.toArray(new String[0]);
    }

    public static RedirectResult parseRedirection(String[] arguments, String currentDir)
    {
        boolean isOutputRedirectedToFile = false;
        boolean isOutputRedirectedToFileAppend = false;
        String outputFilePath = "";
        File outputFile = null;

        boolean isErrRedirectedToFile = false;
        boolean isErrRedirectedToFileAppend = false;
        String errFilePath = "";
        File errFile = null;

        List<String> tmp = new ArrayList<>();
        for(int i=0; i<arguments.length; i++)
        {
            if((">".equals(arguments[i]) || "1>".equals(arguments[i])) && i+1<arguments.length) {
                isOutputRedirectedToFile = true;
                outputFilePath = arguments[i+1];
                i++;
            }
            else if("2>".equals(arguments[i]) && i+1<arguments.length) {
                isErrRedirectedToFile = true;
                errFilePath = arguments[i+1];
                i++;
            }
            else if((">>".equals(arguments[i]) || "1>>".equals(arguments[i])) && i+1<arguments.length)
            {
                isOutputRedirectedToFileAppend = true;
                outputFilePath = arguments[i+1];
                i++;
            }
            else if("2>>".equals(arguments[i]) && i+1<arguments.length) {
                isErrRedirectedToFileAppend = true;
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
        }
        if(isOutputRedirectedToFileAppend)
        {
            outputFile = new File(currentDir).toPath().resolve(outputFilePath).toFile();
        }
        if(isErrRedirectedToFile)
        {
            errFile = new File(currentDir).toPath().resolve(errFilePath).toFile();
        }
        if(isErrRedirectedToFileAppend)
        {
            errFile = new File(currentDir).toPath().resolve(errFilePath).toFile();
        }

        RedirectResult redirectResult = new RedirectResult(arguments, outputFile, isOutputRedirectedToFileAppend, errFile, isErrRedirectedToFileAppend);
        return redirectResult;
    }
    public static PrintStream getOutStream(RedirectResult redirectResult) throws FileNotFoundException {
        PrintStream out = System.out;
        if(redirectResult.getOutputFile() !=null)
        {
            if(redirectResult.isAppendOutput())
            {
                out = new PrintStream(new FileOutputStream(redirectResult.getOutputFile(), true));
            }
            else
            {
                out = new PrintStream(redirectResult.getOutputFile());
            }
        }
        return out;
    }

    public static PrintStream getErrStream(RedirectResult redirectResult) throws FileNotFoundException {
        PrintStream err = System.err;
        if(redirectResult.getErrFile() !=null)
        {
            if(redirectResult.isAppendErr())
            {
                err = new PrintStream(new FileOutputStream(redirectResult.getErrFile(), true));
            }
            else
            {
                err = new PrintStream(redirectResult.getErrFile());
            }
        }
        return err;
    }

}
