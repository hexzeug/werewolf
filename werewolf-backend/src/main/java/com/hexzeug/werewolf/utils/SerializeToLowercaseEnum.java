package com.hexzeug.werewolf.utils;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * Enums can implement this interface to make them serialize to lowercase values when using Jackson to serialize them.
 */
public interface SerializeToLowercaseEnum {
    @JsonValue
    default String getValue() {
        return toString().toLowerCase(Locale.ROOT);
    }
}
