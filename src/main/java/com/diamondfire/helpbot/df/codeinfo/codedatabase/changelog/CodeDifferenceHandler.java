package com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class CodeDifferenceHandler {
    
    static StringBuilder differences = new StringBuilder();
    static ArrayList<String> differs = new ArrayList<>();
    
    public static void refresh() {
        differences = new StringBuilder();
        differs = new ArrayList<>();
        try {
            generateDifferences();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void setComparer(File toCompare) {
        try {
            Files.copy(toCompare.toPath(), ExternalFiles.DB_COMPARE.toPath(), StandardCopyOption.REPLACE_EXISTING);
            refresh();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void generateDifferences() throws IOException {
        
        BufferedReader txtReader = new BufferedReader(new FileReader(ExternalFiles.DB_COMPARE.getPath()));
        BufferedReader txtReader2 = new BufferedReader(new FileReader(ExternalFiles.DB.getPath()));
        
        List<JsonObject> jsonObjects = readDiff(txtReader,txtReader2);
        
        //Clean up here maybe possible but it isn't that bad
        compare(jsonObjects.get(0).get("codeblocks").getAsJsonArray(), jsonObjects.get(1).get("codeblocks").getAsJsonArray(), "name");
        compare(jsonObjects.get(0).get("actions").getAsJsonArray(), jsonObjects.get(1).get("actions").getAsJsonArray(), "name");
        compare(jsonObjects.get(0).get("gameValues").getAsJsonArray(), jsonObjects.get(1).get("gameValues").getAsJsonArray(), "name");
        compare(jsonObjects.get(0).get("particles").getAsJsonArray(), jsonObjects.get(1).get("particles").getAsJsonArray(), "particle");
        compare(jsonObjects.get(0).get("potions").getAsJsonArray(), jsonObjects.get(1).get("potions").getAsJsonArray(), "potion");
        compare(jsonObjects.get(0).get("sounds").getAsJsonArray(), jsonObjects.get(1).get("sounds").getAsJsonArray(), "sound");
        if (differs.size() >= 20) {
            differences.append(String.format("\n+ %s Modifications...", differs.size()));
        } else {
            differs.forEach((s -> differences.append(s)));
        }
        
        
    }
    
    public static String getDifferences() {
        
        if (differences.toString().length() == 0) {
            return "Nothing new here!";
        }
        return differences.toString();
    }
    
    private static HashMap<String, JsonObject> generateHashMap(JsonArray array, String keyName) {
        HashMap<String, JsonObject> objectHashMap = new HashMap<>();
        for (JsonElement element : array) {
            if (element == null) {
                continue;
            }
            JsonElement nameElement;
            if (element.getAsJsonObject().get(keyName) == null) {
                nameElement = element.getAsJsonObject().get("icon").getAsJsonObject().get("name");
            } else {
                nameElement = element.getAsJsonObject().get(keyName);
            }
            objectHashMap.put(nameElement.getAsString(), element.getAsJsonObject());
        }
        return objectHashMap;
    }
    
    private static void compare(JsonArray array, JsonArray oldArray, String keyName) {
        try {
            // Setup the reader to prevent parsing problems.
            HashMap<String, JsonObject> objectHashMap = generateHashMap(array, keyName);
            HashMap<String, JsonObject> objectHashMap2 = generateHashMap(oldArray, keyName);
            
            for (String key : objectHashMap.keySet()) {
                if (!objectHashMap2.containsKey(key)) {
                    //That means that a new action has appeared!
                    differences.append("\n+ New ").append(key);
                    
                } else {
                    if (!objectHashMap.get(key).equals(objectHashMap2.get(key))) {
                        differs.add("\n= Modified " + key);
                    }
                }
            }
            
            for (String key : objectHashMap2.keySet()) {
                if (!objectHashMap.containsKey(key)) {
                    //That means that an action was deleted.
                    differences.append("\n- Removed ").append(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Does stuff with readers to parse old and new JSON data
     *
     * @param readers - readers being parsed
     * @return parsed JsonObjects
     * @throws IOException {@link BufferedReader#close()}
     */
    private static List<JsonObject> readDiff(BufferedReader... readers) throws IOException {
        List<JsonObject> jsonObjects = new ArrayList<>();
        
        for (BufferedReader reader : readers) {
            String rawJson = reader.lines().collect(Collectors.joining());
            reader.close();
            
            JsonReader readerJson = new JsonReader(new StringReader(rawJson));
            
            JsonObject jsonObject = null;
            try {
                jsonObject = JsonParser.parseReader(readerJson).getAsJsonObject();
            } catch (Exception e) {
                System.out.println("Old db is corrupted, rewriting!");
                Files.copy(ExternalFiles.DB.toPath(), ExternalFiles.DB_COMPARE.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            jsonObjects.add(jsonObject);
        }
        
        
        return jsonObjects;
    }
}


