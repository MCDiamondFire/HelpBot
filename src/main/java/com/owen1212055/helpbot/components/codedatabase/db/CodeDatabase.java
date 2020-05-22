package com.owen1212055.helpbot.components.codedatabase.db;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.owen1212055.helpbot.components.ExternalFileHandler;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.*;
import com.owen1212055.helpbot.util.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private static final HashMap<String, String> codeblockIdentifierRegistry = new HashMap<>();

    /**
     * Starts the initialization of the code database. This loads everything needed!
     */
    public static void initialize() {
        codeBlocks.clear();
        actions.clear();
        deprecatedActions.clear();
        sounds.clear();
        potions.clear();
        gameValues.clear();
        codeblockIdentifierRegistry.clear();
        deprecatedGameValues.clear();
        System.out.println("Starting code database...");
        try (BufferedReader txtReader = new BufferedReader(new FileReader(ExternalFileHandler.DB.getPath()))) {
            String json = txtReader.lines().collect(Collectors.joining());

            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            // Setup the reader to prevent parsing problems.
            JsonObject mainMap = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            CodeDatabase.initDatabase(mainMap);
        } catch (Exception e) {
            System.out.println("Could not start codeblock database! Exception occurred....");
            Util.error(e, "Could not start codeblock database!");
            e.printStackTrace();
        }
        System.out.println("Database loaded!");
    }

    /**
     * Generates the codeblock database.
     */
    private static void generateCodeblocks(JsonObject data) {
        for (JsonElement codeBlock : data.getAsJsonArray("codeblocks")) {
            CodeBlockData finalCodeBlock = new CodeBlockData(codeBlock.getAsJsonObject());
            codeblockIdentifierRegistry.put(finalCodeBlock.getName(), finalCodeBlock.getIdentifier());
            codeBlocks.add(finalCodeBlock);
        }
    }

    /**
     * Generates the codeblock ACTION database.
     */
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

    /**
     * Does a basic deprecation check... Will need to communicate with the developers to see if I can improve this.
     */
    private static boolean deprecationCheck(SimpleData data) {

        return !data.getItem().getMaterial().equals("STONE");
    }

    /**
     * Sees if this codeblock action may be dependent on a dynamic codeblock.
     */
    private static boolean associationCheck(CodeBlockActionData data) {
        return data.getName().equals("dynamic");

    }


    /**
     * Generates the sound database.
     */
    private static void generateSounds(JsonObject data) {
        for (JsonElement sound : data.get("sounds").getAsJsonArray()) {
            SoundData finalAction = new SoundData(sound.getAsJsonObject());
            sounds.add(finalAction);
        }
    }

    /**
     * Generates the potion database.
     */
    private static void generatePotions(JsonObject data) {
        for (JsonElement potion : data.get("potions").getAsJsonArray()) {
            PotionData finalPotion = new PotionData(potion.getAsJsonObject());
            potions.add(finalPotion);
        }
    }

    /**
     * Generates the particle database.
     */
    private static void generateParticles(JsonObject data) {
        for (JsonElement particle : data.get("particles").getAsJsonArray()) {
            ParticleData finalParticle = new ParticleData(particle.getAsJsonObject());
            particles.add(finalParticle);
        }
    }

    /**
     * Generates the gamevalue database.
     */
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

    /**
     * Generates the whole database in a neat little package.
     *
     * @param data The raw JSON data.
     */
    public static void initDatabase(JsonObject data) {
        generateCodeblocks(data);
        generateActions(data);
        generateParticles(data);
        generatePotions(data);
        generateSounds(data);
        generateGameValues(data);

    }

    /**
     * @return Returns the CodeBlock ACTION database.
     */
    public static List<CodeBlockActionData> getActions() {
        return actions;
    }


    public static List<SimpleData> getSimpleData() {
        ArrayList<SimpleData> array = new ArrayList<>();
        array.addAll(getCodeBlocks());
        array.addAll(getActions());
        array.addAll(getGameValues());
        // array.addAll(getSounds());


        return array;
    }

    public static CodeBlockActionData fetchCodeBlockAction(String codeblockAction) {
        CodeBlockActionData data = getActions().stream()
                .filter((block) -> block.getName().equals(codeblockAction))
                .findFirst()
                .orElse(null);
        return data;
    }

    public static CodeBlockActionData fetchCodeBlockSubAction(String codeblockName) {
        CodeBlockActionData data = getActions().stream()
                .filter((block) -> Arrays.asList(block.getAliases()).contains(codeblockName))
                .findFirst()
                .orElse(null);

        if (data == null) {
            data = getActions().stream()
                    .filter((block) -> block.getName().equals(codeblockName))
                    .findFirst()
                    .orElse(null);
        }
        return data;
    }

    /**
     * @return Returns the CodeBlock database.
     */
    public static List<CodeBlockData> getCodeBlocks() {
        return codeBlocks;
    }

    /**
     * @return Returns the particle database.
     */
    public static List<ParticleData> getParticles() {
        return particles;
    }

    /**
     * @return Returns the potion database.
     */
    public static List<PotionData> getPotions() {
        return potions;
    }

    /**
     * @return Returns the sound database.
     */
    public static List<SoundData> getSounds() {
        return sounds;
    }

    /**
     * @return Returns the gamevalue database.
     */
    public static List<GameValueData> getGameValues() {
        return gameValues;
    }

    /**
     * @return Returns the registry of all codeblock identifiers in the database.
     */
    public static HashMap<String, String> getCodeblockIdentifierRegistry() {
        return codeblockIdentifierRegistry;
    }

    public static List<CodeBlockActionData> getDeprecatedActions() {
        return deprecatedActions;
    }

    public static List<GameValueData> getDeprecatedGameValues() {
        return deprecatedGameValues;
    }
}


