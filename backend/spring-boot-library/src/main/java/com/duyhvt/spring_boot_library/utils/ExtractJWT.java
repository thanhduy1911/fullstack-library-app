package com.duyhvt.spring_boot_library.utils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ExtractJWT {
    public static String payloadJWTExtraction(String token, String key) {
        token.replace("Bearer ", "");

        String[] parts = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String payload = new String(decoder.decode(parts[1]));

        String[] entries = payload.split(",");
        Map<String, String> map = new HashMap<>();

        for (String entry : entries) {
            String[] keyValue = entry.split(":");

            if (keyValue[0].equals("\"" + key + "\"")) {

                int remove = 1;
                if (keyValue[1].endsWith("}")) {
                    remove = 2;
                }
                keyValue[1] = keyValue[1].substring(0, keyValue[1].length() - remove);
                keyValue[1] = keyValue[1].substring(1);

                keyValue[0] = keyValue[0].substring(0, keyValue[0].length() - 1);
                keyValue[0] = keyValue[0].substring(1);

                map.put(keyValue[0], keyValue[1]);
            }
        }

        return map.get(key);
    }
}
