package com.kantisiri;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MojangAPIHelper {

    private static final ExecutorService executor = Executors.newCachedThreadPool();
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
                return null;
            }
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(result.toString());
            return jsonObject.get("id").toString().replace("\"", "");
        }, executor);
    }

}
