package com.kantisiri;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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
                return new Pair<>(null, null);
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) jsonParser.parse(result.toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
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
                    Bukkit.getLogger().log(Level.SEVERE, "Could not find UUID " + uuid + " with Mojang API!");
                }

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                try {
                    jsonObject = (JSONObject) jsonParser.parse(result.toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                names.add(jsonObject.get("name").toString().replace("\"", ""));
            }

            return names;
        });
    }

}
