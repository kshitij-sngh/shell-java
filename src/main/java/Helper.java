import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
