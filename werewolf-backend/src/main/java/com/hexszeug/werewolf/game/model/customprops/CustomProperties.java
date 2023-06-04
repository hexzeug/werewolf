package com.hexszeug.werewolf.game.model.customprops;

/**
 * The {@code CustomProperties} interface provides methods to manipulate custom properties with key-value pairs,
 * thus classes implementing this interface should be some kind of data representation.
 * The values can be of any type even different types for different values.
 * <p>
 * Please note that sub-interfaces or implementing classes may
 * override this behavior.
 * Values might e.g. have to be serializable.
 * Please consider looking at the docs of the implementation for determining which value types are allowed.
 * </p>
 */
public interface CustomProperties {
    /**
     * Sets a custom property with the given key to the given value. If the property already exists it is replaced,
     * otherwise it is created.
     * @param key The key for later reading the property back
     * @param value The value to set the property to
     */
    void set(String key, Object value);

    /**
     * Retrieves the value associated with the specified key and returns it as the specified class type.
     *
     * @param key   the key to look up the value
     * @param clazz the class type to which the value should be cast
     * @param <T>   the type of the value to be returned
     * @return the value associated with the key, cast to the specified class type
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * Deletes the value associated with the specified key.
     *
     * @param key the key for which the value should be deleted
     */
    void delete(String key);
}
