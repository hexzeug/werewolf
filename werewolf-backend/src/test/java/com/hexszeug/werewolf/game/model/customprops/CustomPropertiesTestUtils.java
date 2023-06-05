package com.hexszeug.werewolf.game.model.customprops;

import java.io.Serial;
import java.io.Serializable;

public final class CustomPropertiesTestUtils {
    public static final Object VALUE_OBJECT = new Object();
    public static final String VALUE_STRING = "";
    public static final CustomType VALUE_CUSTOM = new CustomType();
    public static final SerializableType VALUE_SERIALIZABLE = new SerializableType();
    public static final int VALUE_INT = 0;

    public static final Class<CustomType> TYPE_CUSTOM = CustomType.class;
    public static final Class<SerializableType> TYPE_SERIALIZABLE = SerializableType.class;

    private CustomPropertiesTestUtils() {}

    public static final class CustomType {}
    public static final class SerializableType implements Serializable {}
}