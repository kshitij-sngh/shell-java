import java.io.File;
import java.io.PrintStream;

public class RedirectResult {
    String[] cleanedArgs;

    File outputFile = null;
    boolean appendOutput = false;

    File errFile = null;
    boolean appendErr = false;

    public RedirectResult(String[] cleanedArgs, File outputFile, boolean appendOutput, File errFile, boolean appendErr) {
        this.cleanedArgs = cleanedArgs;
        this.outputFile = outputFile;
        this.appendOutput = appendOutput;
        this.errFile = errFile;
        this.appendErr = appendErr;
    }

    public String[] getCleanedArgs() {
        return cleanedArgs;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public boolean isAppendOutput() {
        return appendOutput;
    }

    public File getErrFile() {
        return errFile;
    }

    public boolean isAppendErr() {
        return appendErr;
    }
}
