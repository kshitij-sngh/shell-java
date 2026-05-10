import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    public static final Map<String, CommandAction> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("exit", (arguments, out, err, dir)->{
            return Constants.SHUTDOWN_SIGNAL;
        });

        COMMANDS.put("echo", (arguments, out, err, dir)->{
            if (arguments.length > 1) {
                out.print(arguments[1]);
                for (int i = 2; i < arguments.length; i++)
                    out.print(" " + arguments[i]);
            }
            out.println();
            return dir;
        });

        COMMANDS.put("type", (arguments, out, err, dir)->{
            if (arguments.length >= 2) {
                String subCmd = arguments[1];
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
            return dir;
        });

        COMMANDS.put("pwd", (arguments, out, err, dir)->{
            out.println(dir);
            return dir;
        });

        COMMANDS.put("cd", (arguments, out, err, dir)->{
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
                    targetPath = Path.of(dir).resolve(cdArg).normalize();
                }

                if (Files.isDirectory(targetPath)) {
                    dir = targetPath.toAbsolutePath().toString();
                } else
                    err.println("cd: " + cdArg + ": No such file or directory");

            }
            return dir;
        });
    }

    public static CommandAction getCommandAction(String cmdName)
    {
        return COMMANDS.get(cmdName);
    }


}
