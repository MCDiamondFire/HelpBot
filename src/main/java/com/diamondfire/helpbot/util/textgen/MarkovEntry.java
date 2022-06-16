package com.diamondfire.helpbot.util.textgen;

import java.util.*;
import java.util.stream.Collectors;

public class MarkovEntry {
    public final String word;
    public final int totalSeen;
    public final Map<String, Float> probabilities;
    
    public MarkovEntry(String word, int totalSeen, Map<String, Float> probabilities) {
        this.word = word;
        this.totalSeen = totalSeen;
        this.probabilities = probabilities;
    }
    
    public String write() {
        List<String> elements = new ArrayList<>();
        
        elements.add(word + "/" + totalSeen);
        for (Map.Entry<String, Float> probability : probabilities.entrySet()) {
            elements.add(probability.getKey() + "/" + probability.getValue());
        }
        
        return String.join("//", elements);
    }
    
    public static MarkovEntry read(String line) {
        String[] elements = line.split("//");
        String[] metaElement = elements[0].split("/");
        
        String word = metaElement[0];
        int totalSeen = Integer.parseInt(metaElement[1]);
        Map<String, Float> probabilities = Arrays.stream(elements)
                .skip(1)
                .map(s -> s.split("/"))
                .collect(Collectors.toMap(l -> l[0], l -> Float.parseFloat(l[1])));
        
        return new MarkovEntry(word, totalSeen, probabilities);
    }
    
    protected static class Builder {
        public final String word;
        public final List<String> followers = new ArrayList<>();
        
        public Builder(String word) {
            this.word = word;
        }
        
        public MarkovEntry build() {
            int total = followers.size();
            Map<String, Float> probabilities = followers.stream()
                    .collect(Collectors.groupingBy(
                            s -> s,
                            Collectors.collectingAndThen(
                                    Collectors.counting(),
                                    n -> n / (float) total
                            )
                    ));
            
            
            return new MarkovEntry(word, total, probabilities);
        }
    }
}