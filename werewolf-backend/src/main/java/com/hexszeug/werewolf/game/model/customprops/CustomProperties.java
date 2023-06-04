package com.hexszeug.werewolf.game.model.customprops;

public interface CustomProperties {
    <T> void set(String key, T value);
    <T> T get(String key, Class<T> clazz);
    void delete(String key);
}
