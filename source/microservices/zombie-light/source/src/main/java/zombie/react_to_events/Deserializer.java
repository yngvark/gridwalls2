package zombie.react_to_events;

import com.google.gson.Gson;

class Deserializer {
    private final Gson gson;

    public Deserializer(Gson gson) {
        this.gson = gson;
    }

    public <T> T deserialize(String data, Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }
}
