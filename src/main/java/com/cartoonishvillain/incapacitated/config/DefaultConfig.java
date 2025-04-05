package com.cartoonishvillain.incapacitated.config;

public class DefaultConfig {
    public static String provider( String name ) {
        return """
                # Incapacitated client config.
                # For the remainder of the configuration, use incapacitated.json.
                # Does the player screen desaturate on their last down? (This is bugged in craftmine when you also regain downs from completion of the mine)
                lastDownDesaturate=false
                """;
    }
}
