package com.diamondfire.helpbot.df.codeinfo.codedatabase.db;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.sys.externalfile.ExternalFile;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.util.*;

public class CodeDatabase {

    public static final String CODEBLOCKS = "codeblocks";
    public static final String ACTIONS = "actions";
    public static final String DEPRECATED_ACTIONS = "dactions";
    public static final String POTIONS = "potions";
    public static final String SOUNDS = "sounds";
    public static final String PARTICLES = "particles";
    public static final String GAME_VALUES = "gamevalues";
    public static final String DEPRECATED_GAME_VALUES = "dgamevalues";
    private static final Map<String, List<? extends CodeObject>> registry = new HashMap<>();

    public static void initialize() {
        registry.clear();

        System.out.println("Starting code database...");
        try (JsonReader reader = new JsonReader(new FileReader(ExternalFile.DB.getFile()))) {
            reader.setLenient(true);

            CodeDatabase.initDatabase(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (Exception e) {
            System.out.println("Could not start codeblock database! Exception occurred....");
            Util.error(e, "Could not start codeblock database!");
            e.printStackTrace();
        }
        System.out.println("Database loaded!");
    }

    private static void generateCodeblocks(JsonObject data) {
        List<CodeBlockData> codeBlocks = newRegistry(CODEBLOCKS);

        for (JsonElement codeBlock : data.getAsJsonArray("codeblocks")) {
            CodeBlockData finalCodeBlock = new CodeBlockData(codeBlock.getAsJsonObject());
            codeBlocks.add(finalCodeBlock);
        }
    }

    public static List<CodeObject> getStandardObjects() {
        List<CodeObject> objects = new ArrayList<>();
        objects.addAll(getRegistry(CODEBLOCKS));
        objects.addAll(getRegistry(ACTIONS));
        objects.addAll(getRegistry(GAME_VALUES));

        return objects;
    }

    private static void generateActions(JsonObject data) {
        List<ActionData> actions = newRegistry(ACTIONS);
        List<ActionData> deprecatedActions = newRegistry(DEPRECATED_ACTIONS);

        for (JsonElement action : data.get("actions").getAsJsonArray()) {
            ActionData finalAction = new ActionData(action.getAsJsonObject());

            if (finalAction.getName().equals("dynamic")) {
                List<CodeBlockData> codeBlocks = getRegistry(CODEBLOCKS);
                CodeBlockData codeblockData = null;

                for (CodeBlockData codeBlock : codeBlocks) {
                    if (codeBlock.getName().equals(finalAction.getCodeblockName())) {
                        codeblockData = codeBlock;
                        break;
                    }
                }

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


    private static boolean deprecationCheck(CodeObject data) {
        return !data.getItem().getMaterial().equals("STONE");
    }

    private static void generateSounds(JsonObject data) {
        List<SoundData> sounds = newRegistry(SOUNDS);
        for (JsonElement sound : data.get("sounds").getAsJsonArray()) {
            SoundData finalAction = new SoundData(sound.getAsJsonObject());
            sounds.add(finalAction);
        }
    }


    private static void generatePotions(JsonObject data) {
        List<PotionData> potions = newRegistry(POTIONS);
        for (JsonElement potion : data.get("potions").getAsJsonArray()) {
            PotionData finalPotion = new PotionData(potion.getAsJsonObject());
            potions.add(finalPotion);
        }
    }


    private static void generateParticles(JsonObject data) {
        List<ParticleData> particles = newRegistry(PARTICLES);
        for (JsonElement particle : data.get("particles").getAsJsonArray()) {
            ParticleData finalParticle = new ParticleData(particle.getAsJsonObject());
            particles.add(finalParticle);
        }
    }


    private static void generateGameValues(JsonObject data) {
        List<GameValueData> gameValues = newRegistry(GAME_VALUES);
        List<GameValueData> deprecatedGameValues = newRegistry(DEPRECATED_GAME_VALUES);

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

    private static <T extends CodeObject> List<T> newRegistry(String name) {
        List<T> list = new ArrayList<>();
        registry.put(name, list);

        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T extends CodeObject> List<T> getRegistry(String name) {
        return (List<T>) registry.get(name);
    }
}


