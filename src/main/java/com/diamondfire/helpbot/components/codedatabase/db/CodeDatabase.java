package com.diamondfire.helpbot.components.codedatabase.db;

import com.diamondfire.helpbot.components.ExternalFileHandler;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeDatabase {

    private static final List<CodeBlockData> codeBlocks = new ArrayList<>();
    private static final List<CodeBlockActionData> actions = new ArrayList<>();
    private static final List<CodeBlockActionData> deprecatedActions = new ArrayList<>();
    private static final List<GameValueData> deprecatedGameValues = new ArrayList<>();
    private static final List<SoundData> sounds = new ArrayList<>();
    private static final List<PotionData> potions = new ArrayList<>();
    private static final List<ParticleData> particles = new ArrayList<>();
    private static final List<GameValueData> gameValues = new ArrayList<>();


    public static void initialize() {
        codeBlocks.clear();
        actions.clear();
        deprecatedActions.clear();
        sounds.clear();
        potions.clear();
        gameValues.clear();
        deprecatedGameValues.clear();

        System.out.println("Starting code database...");
        try (BufferedReader txtReader = new BufferedReader(new FileReader(ExternalFileHandler.DB.getPath()));
             JsonReader reader = new JsonReader(new StringReader(txtReader.lines().collect(Collectors.joining())));) {

            reader.setLenient(true);
            // Setup the reader to prevent parsing problems.
            JsonObject mainMap = JsonParser.parseReader(reader).getAsJsonObject();

            CodeDatabase.initDatabase(mainMap);


        } catch (Exception e) {
            System.out.println("Could not start codeblock database! Exception occurred....");
            Util.error(e, "Could not start codeblock database!");
            e.printStackTrace();
        }
        System.out.println("Database loaded!");
    }


    private static void generateCodeblocks(JsonObject data) {
        for (JsonElement codeBlock : data.getAsJsonArray("codeblocks")) {
            CodeBlockData finalCodeBlock = new CodeBlockData(codeBlock.getAsJsonObject());
            codeBlocks.add(finalCodeBlock);
        }
    }


    private static void generateActions(JsonObject data) {
        for (JsonElement action : data.get("actions").getAsJsonArray()) {
            CodeBlockActionData finalAction = new CodeBlockActionData(action.getAsJsonObject());

            if (associationCheck(finalAction)) {
                CodeBlockData codeblockData = getCodeBlocks().stream()
                        .filter((codeBlockData -> codeBlockData.getName().equals(finalAction.getCodeblockName())))
                        .findFirst()
                        .orElse(null);

                if (codeblockData == null) {
                    System.out.println("CodeblockAction " + finalAction + " tripped association check but none could be found.");
                } else {
                    codeblockData.assignAction(finalAction);
                }
                continue;

            }

            if (deprecationCheck(finalAction)) {
                actions.add(finalAction);
            } else {
                deprecatedActions.add(finalAction);
            }

        }
    }


    private static boolean deprecationCheck(SimpleData data) {

        return !data.getItem().getMaterial().equals("STONE");
    }


    private static boolean associationCheck(CodeBlockActionData data) {
        return data.getName().equals("dynamic");

    }


    private static void generateSounds(JsonObject data) {
        for (JsonElement sound : data.get("sounds").getAsJsonArray()) {
            SoundData finalAction = new SoundData(sound.getAsJsonObject());
            sounds.add(finalAction);
        }
    }


    private static void generatePotions(JsonObject data) {
        for (JsonElement potion : data.get("potions").getAsJsonArray()) {
            PotionData finalPotion = new PotionData(potion.getAsJsonObject());
            potions.add(finalPotion);
        }
    }


    private static void generateParticles(JsonObject data) {
        for (JsonElement particle : data.get("particles").getAsJsonArray()) {
            ParticleData finalParticle = new ParticleData(particle.getAsJsonObject());
            particles.add(finalParticle);
        }
    }


    private static void generateGameValues(JsonObject data) {
        for (JsonElement gameValue : data.get("gameValues").getAsJsonArray()) {
            GameValueData finalGameValue = new GameValueData(gameValue.getAsJsonObject());
            if (deprecationCheck(finalGameValue)) {
                gameValues.add(finalGameValue);
            } else {
                deprecatedGameValues.add(finalGameValue);
            }
        }
    }


    public static void initDatabase(JsonObject data) {
        generateCodeblocks(data);
        generateActions(data);
        generateParticles(data);
        generatePotions(data);
        generateSounds(data);
        generateGameValues(data);

    }


    public static List<CodeBlockActionData> getActions() {
        return actions;
    }


    public static List<SimpleData> getSimpleData() {
        ArrayList<SimpleData> array = new ArrayList<>();
        array.addAll(getCodeBlocks());
        array.addAll(getActions());
        array.addAll(getGameValues());

        return array;
    }

    public static List<CodeBlockData> getCodeBlocks() {
        return codeBlocks;
    }

    public static List<ParticleData> getParticles() {
        return particles;
    }

    public static List<PotionData> getPotions() {
        return potions;
    }

    public static List<SoundData> getSounds() {
        return sounds;
    }

    public static List<GameValueData> getGameValues() {
        return gameValues;
    }

    public static List<CodeBlockActionData> getDeprecatedActions() {
        return deprecatedActions;
    }

    public static List<GameValueData> getDeprecatedGameValues() {
        return deprecatedGameValues;
    }
}


