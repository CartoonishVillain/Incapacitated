package com.cartoonishvillain.incapacitated.config;

public class DefaultConfig {
    public static String provider( String name ) {
        return """
                # Incapacitated client config.
                # For the remainder of the configuration, use incapacitated.json.
                # Does the player screen desaturate on their last down?
                lastDownDesaturate=true
                """;
    }
}
