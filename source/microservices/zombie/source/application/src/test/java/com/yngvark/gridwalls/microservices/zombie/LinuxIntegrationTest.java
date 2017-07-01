package com.yngvark.gridwalls.microservices.zombie;

import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@Disabled
class LinuxIntegrationTest {
    private final Logger logger = getLogger(getClass());

//    public App initApp(String to, String from) throws Exception {
//        logger.info(Paths.get(".").toAbsolutePath().toString());
//
//        Path toPath = Paths.get(to);
//        Path fromPath = Paths.get(from);
//        delete(toPath, fromPath);
//
//        Runtime.getRuntime().exec("mkfifo " + to).waitFor();
//        Runtime.getRuntime().exec("mkfifo " + from).waitFor();
//
//        OutputFileOpener outputFileOpener = new OutputFileOpener(to);
//        InputFileOpener inputFileOpener = new InputFileOpener(from);
//
//        logger.info("Opening output.");
//        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
//        logger.info("Opening input: ");
//        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
//
//        logger.info("Streams opened.");
//
//        App network = new App();
//        network.inputFileReader = inputFileReader;
//        network.outputFileWriter = outputFileWriter;
//        network.toPath = toPath;
//        network.fromPath = fromPath;
//        return network;
//    }
//
//    private void delete(Path... paths) throws IOException {
//        for (Path path : paths) {
//            if (Files.exists(path)) {
//                logger.info("Deleting {}", path);
//                Files.delete(path);
//            } else {
//                logger.info("Not deleting {}, couldnt find it.", path);
//            }
//        }
//    }
//
//    class App {
//        InputFileReader inputFileReader;
//        OutputFileWriter outputFileWriter;
//        Path toPath;
//        Path fromPath;
//
//        public void stopAndFreeResources() throws Exception {
//            outputFileWriter.closeStream();
//        }
//    }
//
//    @Test
//    void should_not_crash() throws Exception {
//        String to = "build/to_app";
//        String from = "build/from_app";
//
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        Future game = executorService.submit(() -> {
//            try {
//                Main.main(new String[] { to, from });
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        App app = initApp(to, from);
//
//        Serializer serializer = new JsonSerializer();
//        String mapInfo = serializer.serialize(new MapInfo(10, 6), MapInfo.class);
//        app.outputFileWriter.write(mapInfo);
//
//        Thread.sleep(2000l);
//        app.stopAndFreeResources();
//        game.get();
//        delete(app.toPath, app.fromPath);
//    }

}