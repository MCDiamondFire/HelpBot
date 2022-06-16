package com.diamondfire.helpbot.util.textgen;

import java.util.*;

public class MarkovManipulation {
    private static final Random RANDOM = new Random();
    
    protected Map<String, MarkovEntry> entries = Map.of();
    protected List<String> startingWords = List.of();
    
    public void load(List<String> lines) {
        Map<String, MarkovEntry.Builder> builders = new HashMap<>();
        List<String> newStartingWords = new ArrayList<>();
        
        for (String line : lines) {
            String[] splitLine = line.split(" ");
            for (int i = 0; i < splitLine.length; i++) {
                String word = splitLine[i];
                if (i == 0) newStartingWords.add(word);
                
                MarkovEntry.Builder builder = builders.computeIfAbsent(word, MarkovEntry.Builder::new);
                
                int nextIndex = i + 1;
                String next = nextIndex >= splitLine.length ? "." : splitLine[nextIndex];
                builder.followers.add(next);
            }
        }
        
        Map<String, MarkovEntry> newEntries = new HashMap<>();
        for (MarkovEntry.Builder builder : builders.values()) {
            newEntries.put(builder.word, builder.build());
        }
        
        entries = Collections.unmodifiableMap(newEntries);
        startingWords = Collections.unmodifiableList(newStartingWords);
    }
    
    public String getNextWord(String word) {
        float num = (float) Math.random();
        float currentNum = 0f;
        
        MarkovEntry entry = entries.get(word);
        if (entry == null) return null;
        for (Map.Entry<String, Float> probabilityEntry : entry.probabilities.entrySet()) {
            currentNum += probabilityEntry.getValue();
            
            if (currentNum > num) {
                String nextWord = probabilityEntry.getKey();
                return Objects.equals(nextWord, ".") ? null : nextWord;
            }
        }
        
        throw new IllegalStateException("Did not find valid next word.");
    }
    
    public String generateFull(int wordLimit) {
        return generateFull(wordLimit, startingWords.get(RANDOM.nextInt(startingWords.size())));
    }
    
    public String generateFull(int wordLimit, String startingWord) {
        StringBuilder builder = new StringBuilder(startingWord);
        
        String word = startingWord;
        for (int i = 0; i < wordLimit; i++) {
            word = getNextWord(word);
        
            if (word == null) break;
            builder.append(" ").append(word);
        }
        
        return builder.toString();
    }
}
