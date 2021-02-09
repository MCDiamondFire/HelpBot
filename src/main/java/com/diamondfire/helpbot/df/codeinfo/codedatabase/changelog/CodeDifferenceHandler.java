package com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class CodeDifferenceHandler {
    
    //TODO Cleanup
    
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
        String json = txtReader.lines().collect(Collectors.joining());
        txtReader.close();
        
        JsonReader reader = new JsonReader(new StringReader(json));
        //reader.setLenient(true);
        
        BufferedReader txtReader2 = new BufferedReader(new FileReader(ExternalFiles.DB.getPath()));
        String json2 = txtReader2.lines().collect(Collectors.joining());
        txtReader2.close();
        
        JsonReader reader2 = new JsonReader(new StringReader(json2));
        //reader2.setLenient(true);
        
        JsonObject objectOld;
        try {
            objectOld = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            System.out.println("Old db is corrupted, rewriting!");
            Files.copy(ExternalFiles.DB.toPath(), ExternalFiles.DB_COMPARE.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        // Setup the reader to prevent parsing problems.
        
        JsonObject object = JsonParser.parseReader(reader2).getAsJsonObject();
        
        // object is new
        // object old is old instance
        
        
        compare(object.get("codeblocks").getAsJsonArray(), objectOld.get("codeblocks").getAsJsonArray(), "name");
        compare(object.get("actions").getAsJsonArray(), objectOld.get("actions").getAsJsonArray(), "name");
        compare(object.get("gameValues").getAsJsonArray(), objectOld.get("gameValues").getAsJsonArray(), "name");
        compare(object.get("particles").getAsJsonArray(), objectOld.get("particles").getAsJsonArray(), "particle");
        compare(object.get("potions").getAsJsonArray(), objectOld.get("potions").getAsJsonArray(), "potion");
        compare(object.get("sounds").getAsJsonArray(), objectOld.get("sounds").getAsJsonArray(), "sound");
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
}


