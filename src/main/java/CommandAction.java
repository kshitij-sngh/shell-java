import java.io.PrintStream;

@FunctionalInterface
public interface CommandAction {
    String execute(String[] arguments, PrintStream out, PrintStream err, String currDir);
}
