package com.yngvark.gridwalls.microservices.zombie2.app;

import com.yngvark.communicate_through_named_pipes.RetrySleeper;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie2.game.Game;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;

import static org.slf4j.LoggerFactory.getLogger;

public class App {
    private final Logger logger = getLogger(getClass());
    private final ExecutorService executorService;
    private final InputFileOpener netcomReaderOpener;
    private final OutputFileOpener netcomWriterOpener;
    private final RetrySleeper retrySleeper;

    private Game game;
    private InputFileReader netcomReader;

    public static App create(
            ExecutorService executorService,
            InputFileOpener inputFileOpener,
            OutputFileOpener outputFileOpener) {
        return new App(
                executorService,
                inputFileOpener,
                outputFileOpener,
                () -> Thread.sleep(1000));
    }

    public App(ExecutorService executorService,
            InputFileOpener netcomReaderOpener,
            OutputFileOpener netcomWriterOpener, RetrySleeper retrySleeper) {
        this.executorService = executorService;
        this.netcomReaderOpener = netcomReaderOpener;
        this.netcomWriterOpener = netcomWriterOpener;
        this.retrySleeper = retrySleeper;
    }

    public void run() throws Throwable {
        logger.info("Starting zombie logic.");

        InputFileReader netcomReader = netcomReaderOpener.openStream(retrySleeper);
        OutputFileWriter netcomWriter = netcomWriterOpener.openStream(retrySleeper);

        Game game = new Game(netcomWriter);
        game.init();
        netcomReader.consume(game);

        netcomWriter.closeStream();
        netcomReader.closeStream();
    }

}
