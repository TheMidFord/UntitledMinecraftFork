package net.minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class SkinFix {
    private static final HashMap<String, SkinFixPlayer> players = new HashMap<>();

    public static String getSkinURL(String username){
        SkinFixPlayer player = getPlayer(username);
        if (player == null){
            return "https://assets.mojang.com/SkinTemplates/steve.png";
        }
        return player.skinURL;
    }

    public static String getCapeURL(String username){
        SkinFixPlayer player = getPlayer(username);
        if (player == null){
            return null;
        }
        return player.capeURL;
    }

    private static SkinFixPlayer getPlayer(String username){
        if (players.containsKey(username)){
            return players.get(username);
        }

        String json = httpGetClean("https://api.mojang.com/users/profiles/minecraft/" + username);
        String uuid = getJsonField(json, "id");
        if (uuid == null){
            return null;
        }
        json = httpGetClean("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        String valueEncoded = getJsonField(json,"value");
        if (valueEncoded == null){
            return null;
        }
        String valueDecoded = clean(new String(Base64.getDecoder().decode(valueEncoded.getBytes())));
        String skinURL = getJsonField(valueDecoded, "SKIN\":{\"url");
        String capeURL = getJsonField(valueDecoded, "CAPE\":{\"url");
        if (skinURL == null){
            return null;
        }
        skinURL = skinURL.replace("http://","https://");
        if (capeURL != null){
            capeURL = capeURL.replace("http://", "https://");
        }
        SkinFixPlayer player = new SkinFixPlayer(username,uuid,skinURL,capeURL);
        players.put(username,player);
        return player;
    }

    private static String getJsonField(String json, String field){
        int len = field.length() + 4;
        int i = json.indexOf("\"" + field + "\":\"");
        if (i == -1){
            return null;
        }
        int j = json.indexOf("\"", i+len);
        return json.substring(i+len, j);

    }

    private static String clean(String str){
        return str.replaceAll("\n","").replaceAll(" ","");
    }

    private static String httpGet(String url) throws IOException {
        InputStream stream = new URL(url).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        StringBuilder stringBuilder = new StringBuilder();
        int data;
        while ((data = reader.read()) != -1){
            stringBuilder.append((char)data);
        }
        return stringBuilder.toString();
    }

    private static String httpGetClean(String url){
        try {
            return clean(httpGet(url));
        } catch (IOException e){
            return "";
        }
    }
}
