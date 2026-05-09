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
        char ch;

        for(int i=0; i<line.length(); i++)
        {
            ch = line.charAt(i);
            if('\''==ch)
            {
                insideSingleQuotes=!insideSingleQuotes;
            }
            else if(' '==ch && !insideSingleQuotes)
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

        return (String[]) args.toArray();
    }
}
