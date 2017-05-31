package to_be_deleted;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CommandExecutorFactory {
    public CommandExecutor create(Process process) {
        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader appOutStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
        CommandExecutor commandExecutor = new CommandExecutor(appInputStream, appOutStream);
        return commandExecutor;
    }
}
