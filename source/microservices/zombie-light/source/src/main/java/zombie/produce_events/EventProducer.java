package zombie.produce_events;

import com.google.gson.Gson;
import zombie.gameloop.Event;
import zombie.move_zombie.Zombie;

import java.io.BufferedWriter;
import java.io.IOException;

public class EventProducer {
    private final Zombie zombie;
    private final Serializer serializer;
    private final BufferedWriter bufferedWriter;

    public EventProducer(Zombie zombie, Serializer serializer, BufferedWriter bufferedWriter) {
        this.zombie = zombie;
        this.serializer = serializer;
        this.bufferedWriter = bufferedWriter;
    }

    public static EventProducer create(Zombie zombie, BufferedWriter bufferedWriter) {
        return new EventProducer(
                zombie,
                new Serializer(new Gson()),
                bufferedWriter);
    }

    public void produce() {
        Event event = zombie.move();
        String serializedEvent = serializer.serialize(event);
        write(serializedEvent);
    }

    private void write(String eventSerialized) {
        try {
            bufferedWriter.write(eventSerialized);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
