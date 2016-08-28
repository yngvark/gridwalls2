package zombie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessTest {
    private BufferedReader inputReader;
    private BufferedWriter outputWriter;

    @BeforeEach
    public void setUp() throws Exception {
        //inputReader = new BufferedReader(new InputStreamReader(System.in));

        //inputReader = new FileReader("");
//        outputWriter = new BufferedWriter(new OutputStreamWriter(System.out));
    }


    @Test
    public void arne() {
        Scanner s = new Scanner("Enter something");
        checkNext(s);
    }

    @Test
    public void arne2() {
        Scanner s = new Scanner(System.in);
        checkNext(s);
    }

    private void checkNext(Scanner s) {
        boolean hasNext = s.hasNext("..ter");
        System.out.println("hanext: " + hasNext);
        String next = s.next();
        System.out.println("hanext: " + next);
    }

    @Test
    public void should_get_version() throws IOException, InterruptedException {
        System.out.println("Opening in_pipe...");
        BufferedWriter appInputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/mnt/src/source/microservices/zombie/container/app/in_pipe")));
        System.out.println("Opening out_pipe...");
        FileInputStream appOutStream = new FileInputStream("/mnt/src/source/microservices/zombie/container/app/out_pipe");

        readFromScanner(appOutStream, appInputStream);

        // String version = "not set";
        //assertTrue(version.startsWith("Version: Zombie"));
    }

    private void readFromScanner(FileInputStream appOutStream, BufferedWriter appInputStream) throws IOException {
        Scanner outputFromProgram = new Scanner(appOutStream);

        System.out.println("Scanner waiting for output...");
        while (outputFromProgram.hasNextLine()) {
            String line = outputFromProgram.nextLine();
            System.out.println("Received line: " + line);

            if (line.contains("Enter something:")) {
                System.out.println("We found something. Line: " + line);

                write(appInputStream, "version");
                break;
            }
        }

        boolean foundVersion = false;

        while (outputFromProgram.hasNextLine()) {
            String line = outputFromProgram.nextLine();
            System.out.println("Received line: " + line);
            if (line.contains("Version: Zombie")) {
                foundVersion = true;
                break;
            }
        }

        write(appInputStream, "exit");
        assertTrue(foundVersion);
    }

    private void write(BufferedWriter appInputStream, String text) throws IOException {
        System.out.println("Writing: " + text);
        appInputStream.write(String.format(text + "%n"));
        appInputStream.flush();
    }

    private void readFromBuffer(FileInputStream fs) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
        StringBuffer buffer = new StringBuffer();

        while (true) {
            //if (reader.ready())
            int returnValue = reader.read();
            if (returnValue == -1) {
                System.out.println("Got -1 from read. What says: " + reader.ready());
                break;
            }

            char c = (char) returnValue;
            buffer.append(c);

            int i = -2;
            if ((i = buffer.indexOf("Enter something: ")) != -1) {
                System.out.println("Got a 'Enter something: ' at: " + i);
                System.out.println(buffer.toString());
                buffer.delete(0, buffer.length());
            }

        }
    }

    private void readDirectly(FileInputStream fs) throws IOException, InterruptedException {
        byte[] bytes = new byte[10000];

        fs.read(bytes);
        System.out.println("Buffer contents: " + new String(bytes));
        Thread.sleep(1000);
        fs.read(bytes);
        System.out.println("Buffer contents: " + new String(bytes));
    }

    @Test
    public void should_exit_withing_10_seconds_when_not_table_to_connect() { // how about logging?

    }

}

