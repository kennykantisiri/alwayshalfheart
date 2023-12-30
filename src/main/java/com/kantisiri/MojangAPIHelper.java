package com.kantisiri;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MojangAPIHelper {

    public static CompletableFuture<Pair<String, String>> getPlayerInfo(String name) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            } catch (Exception e) {
                // When the username doesn't exist, throws Exception -> return null.
                return null;
            }
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(result.toString());
            return new Pair<>(jsonObject.get("name").toString(),
                    jsonObject.get("id").toString().replace("\"", ""));
        });
    }

    public static CompletableFuture<List<String>> convertUUIDToPlayer(List<String> uuids) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> names = new ArrayList<>();
            for (String uuid : uuids) {
                StringBuilder result = new StringBuilder();
                try {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    for (String line; (line = reader.readLine()) != null; ) {
                        result.append(line);
                    }
                } catch (Exception e) {
                    // When the UUID doesn't exist, throws Exception -> return null.
                    return null;
                }

                JsonObject jsonObject = (JsonObject) JsonParser.parseString(result.toString());
                names.add(jsonObject.get("name").toString());
            }

            return names;
        });
    }
    public record Pair<K, V>(K key, V value) {
        // intentionally empty
    }

}
