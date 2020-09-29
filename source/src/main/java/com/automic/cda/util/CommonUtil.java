package com.automic.cda.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class CommonUtil {
	private CommonUtil(){}

    public static List<String> getUniqueValues(List<String> list) {
        Set<String> set = new HashSet<>();
        set.addAll(list);
        List<String> uniqueValues = new ArrayList<>();
        uniqueValues.addAll(set);
        return uniqueValues;
    }

    public static boolean isNotEmpty(String field) {
        return field != null && !field.isEmpty();
    }
    
    public static boolean isValidURL(String url) {
        URL u = null;
        try {
            u = new URL(url);
            u.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    
    public static List<String> getEntityList(JsonArray jDataArray) {
        List<String> entityList = new ArrayList<>();
        for (JsonElement jsonElement : jDataArray) {
            JsonObject jObj = jsonElement.getAsJsonObject();
            entityList.add(jObj.get("name").getAsString());
        }
        return entityList;
    }
    
    public static String trimmedValue(String value){
     	return  value != null ? value.trim() : value;
    }
    
}
