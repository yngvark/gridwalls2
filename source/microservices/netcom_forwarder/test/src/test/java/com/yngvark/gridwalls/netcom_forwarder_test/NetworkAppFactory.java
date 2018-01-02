package com.yngvark.gridwalls.netcom_forwarder_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.rabbitmq.RabbitConnection;
import com.yngvark.named_piped_app_runner.InputStreamListener;
import com.yngvark.named_piped_app_runner.ProcessStarter;
import org.slf4j.Logger;

import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

class NetworkAppFactory {
    public static final Logger logger = getLogger(NetworkAppFactory.class);

    public static NetworkApp start() throws Exception {
        logger.info(Paths.get(".").toAbsolutePath().toString());
        RabbitBrokerConnecter rabbitBrokerConnecter = new RabbitBrokerConnecter(ProcessTest.RABBITMQ_IP);
        RabbitConnection rabbitConnection = rabbitBrokerConnecter.connect();

        String to = Paths.get("build/to_netcom_forwarder").toAbsolutePath().toString();
        String from = Paths.get("build/from_netcom_forwarder").toAbsolutePath().toString();

        Process process = ProcessStarter.startProcess(
                "../source/build/install/app/bin/run",
                to,
                from,
                ProcessTest.RABBITMQ_IP);

        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);
        OutputFileOpener outputFileOpener = new OutputFileOpener(to);

        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        NetworkApp app = new NetworkApp();
        app.host = ProcessTest.RABBITMQ_IP;
        app.rabbitConnection = rabbitConnection;
        app.process = process;
        app.stdoutListener = stdoutListener;
        app.stderrListener = stderrListener;
        app.inputFileReader = inputFileReader;
        app.outputFileWriter = outputFileWriter;
        return app;
    }

}
