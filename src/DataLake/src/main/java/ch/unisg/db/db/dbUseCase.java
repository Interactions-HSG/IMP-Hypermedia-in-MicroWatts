package ch.unisg.db.db;

public interface dbUseCase {
    void put(String key, String value);
    String get(String key);
}
