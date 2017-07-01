package com.yngvark.gridwalls.microservices.zombie;

import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

@Disabled
class IntegrationTest {
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
//        App app = new App();
//        app.inputFileReader = inputFileReader;
//        app.outputFileWriter = outputFileWriter;
//        app.toPath = toPath;
//        app.fromPath = fromPath;
//        return app;
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
//    class Container {
//        MessageListener messageListener;
//    }
////
////    @Test
////    void should_not_crash() throws Exception {
////        // Init app output
////        OutputFileOpener outputFileOpener = mock(OutputFileOpener.class);
////        OutputFileWriter outputFileWriter = mock(OutputFileWriter.class);
////        when(outputFileOpener.openStream(any())).thenReturn(outputFileWriter);
////
////        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
////        doAnswer((invocation -> {
////            String msg = invocation.getArgument(0);
////            queue.add(msg);
////            return Void.TYPE;
////        })).when(outputFileWriter).write(any(String.class));
////
////        // Init app input
////        InputFileOpener inputFileOpener = mock(InputFileOpener.class);
////        InputFileReader inputFileReader = mock(InputFileReader.class);
////        when(inputFileOpener.openStream(any())).thenReturn(inputFileReader);
////
////        Container container = new Container();
////        Object inputFileReaderLock = new Object();
////        doAnswer((invocation) -> {
////            System.out.println("hhei");
////            container.messageListener = invocation.getArgument(0);
////            inputFileReaderLock.wait();
////            return Void.TYPE;
////        }).when(inputFileReader).consume(any(MessageListener.class));
////
////        // Start app
////        ExecutorService executorService = Executors.newCachedThreadPool();
////        Future gameFuture = executorService.submit(() -> {
////            try {
////                Main.main(outputFileOpener, inputFileOpener);
////            } catch (Exception e) {
////                throw new RuntimeException(e);
////            }
////        });
////
////        Serializer serializer = new JsonSerializer();
////        String mapInfo = serializer.serialize(new MapInfo(10, 6), MapInfo.class);
////        outputFileWriter.write(mapInfo);
////
////        Thread.sleep(2000l);
////        app.stopAndFreeResources();
////        gameFuture.get();
////        delete(app.toPath, app.fromPath);
////    }
////
//
//
//    class Container2 {
//        String msg;
//    }
//
//    @Test
//    void should_not_crash_better() throws Exception {
//        // Init app output
//        OutputFileOpener outputFileOpener = mock(OutputFileOpener.class);
//        PipedOutputStream appOutputPipedOutputStream = new PipedOutputStream();
//        OutputFileWriter outputFileWriter = new OutputFileWriter(
//                new BufferedWriter(new OutputStreamWriter(appOutputPipedOutputStream)));
//        when(outputFileOpener.openStream(any())).thenReturn(outputFileWriter);
//
//        PipedInputStream appOutputPipedInputStream = new PipedInputStream();
//        appOutputPipedInputStream.connect(appOutputPipedOutputStream);
//        InputFileReader appOutputReader = new InputFileReader(new BufferedReader(new InputStreamReader(appOutputPipedInputStream)));
//
////        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
////        doAnswer((invocation -> {
////            String msg = invocation.getArgument(0);
////            queue.add(msg);
////            return Void.TYPE;
////        })).when(outputFileWriter).write(any(String.class));
//
//        // Init app input
//        InputFileOpener inputFileOpener = mock(InputFileOpener.class);
//
//        PipedInputStream pipedInputStream = new PipedInputStream();
//        PipedOutputStream pipedOutputStream = new PipedOutputStream();
//        pipedInputStream.connect(pipedOutputStream);
//
//        InputFileReader inputFileReader = new InputFileReader(new BufferedReader(new InputStreamReader(pipedInputStream)));
//        when(inputFileOpener.openStream(any())).thenReturn(inputFileReader);
//
//        OutputFileWriter appInputWriter = new OutputFileWriter(new BufferedWriter(new OutputStreamWriter(pipedOutputStream)));
////
////        Container container = new Container();
////        Object inputFileReaderLock = new Object();
////        doAnswer((invocation) -> {
////            System.out.println("hhei");
////            container.messageListener = invocation.getArgument(0);
////            inputFileReaderLock.wait();
////            return Void.TYPE;
////        }).when(inputFileReader).consume(any(MessageListener.class));
//
//        // Start app
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        Future gameFuture = executorService.submit(() -> {
//            try {
//                Main.main(outputFileOpener, inputFileOpener);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        Container2 container = new Container2();
//        Future getFirstMessageFuture = executorService.submit(() -> {
//            try {
//                appOutputReader.consume((msg) ->  {
//                    container.msg = msg;
//                    appOutputReader.closeStream();
//                });
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        getFirstMessageFuture.get(1, TimeUnit.SECONDS);
//        assertEquals("/subscribeTo MapInfo", container.msg);
//
//        //Serializer serializer = new JsonSerializer();
//        //String mapInfo = serializer.serialize(new MapInfo(10, 6), MapInfo.class);
//        //appInputWriter.write(mapInfo);
//
//        appInputWriter.closeStream();
//        gameFuture.get();
//    }

}