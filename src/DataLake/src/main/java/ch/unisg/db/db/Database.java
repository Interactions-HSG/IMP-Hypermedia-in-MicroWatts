package ch.unisg.db.db;

import java.util.HashMap;

public class Database {
  private static Database instance;
  private final HashMap<String, String> data;

  private Database() {
    this.data = new HashMap<>();
  }

  public static Database getInstance() {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public HashMap<String, String> getAll() {
      return data;
    }
}
