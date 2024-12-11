package ch.unisg.db.db;

import java.util.HashMap;

public class Database {

  private final HashMap<String, String> data;

  private static Database instance;

  private Database() {
    this.data = new HashMap<>();
  }

  public static Database getInstance() {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

  public void addData(String key, String value) {
    data.put(key, value);
  }

  public String getData(String key) {
    return data.get(key);
  }

  public HashMap<String, String> getAllData() {
    return data;
  }
}
