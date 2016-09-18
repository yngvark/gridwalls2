package zombie.lib;

import java.lang.reflect.Field;

/*
 * Taken from http://stackoverflow.com/questions/2950338/how-can-i-kill-a-linux-process-in-java-with-sigkill-process-destroy-does-sigte/2951193#2951193
 * Thanks to Martijn Courteaux, http://stackoverflow.com/users/155137/martijn-courteaux
 */
public class ProcessKiller {
    public static void killUnixProcess(Process process) throws Exception {
        int pid = getUnixPID(process);
        Runtime.getRuntime().exec("kill " + pid).waitFor();
    }

    private static int getUnixPID(Process process) throws Exception {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            Class cl = process.getClass();
            Field field = cl.getDeclaredField("pid");
            field.setAccessible(true);
            Object pidObject = field.get(process);
            return (Integer) pidObject;
        } else {
            throw new IllegalArgumentException("Needs to be a UNIXProcess");
        }
    }
}
