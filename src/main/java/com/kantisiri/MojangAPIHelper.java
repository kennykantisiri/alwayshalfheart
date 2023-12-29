package com.kantisiri;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class MojangAPIHelper {

    public static CompletableFuture<String> getPlayerUUID(String name) {
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
            return jsonObject.get("id").toString().replace("\"", "");
        });
    }

}
