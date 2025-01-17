package ch.unisg.db.db;

public interface dbUseCase {
    void put(String key, String value);
    String getTemperature(String key);
    String getHumidity(String key);
    String get(String key);
}
