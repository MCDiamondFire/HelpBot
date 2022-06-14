package com.diamondfire.helpbot.util.textgen;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MarkovManipulation {
    
    public static String getNextWord(String word) throws IOException {
        
        File file = new File("markov.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        float num = (float) Math.random();
        float currentNum = 0f;
        
        String line;
        String[] split;
        String[] split2;
        while ((line = br.readLine()) != null) {
            
            split = line.split("//");
            if (split[0].split("/")[0].equals(word)) {
                
                for (int i = 1; i < split.length; i++) {
                    
                    split2 = split[i].split("/");
                    currentNum += Float.parseFloat(split2[1]);
                    
                    if (currentNum >= num) {
                        
                        return split2[0];
                    }
                }
            }
        }
        
        return null;
    }
    
    public static void addWord(String word, String word2) throws IOException {
        
        File file = new File("markov.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        ArrayList<String> newFile = new ArrayList<>();
        String newLine;
        float newChance;
        int newAmount;
        
        String line;
        String[] split;
        String[] split2;
        
        boolean addNewWord = true;
        boolean addNewLine = true;
        
        while ((line = br.readLine()) != null) {
            
            split = line.split("//");
            
            String wordTest = split[0].split("/")[0];
            
            newAmount = Integer.parseInt(split[0].split("/")[1]) + 1;
            
            if (wordTest.equals(word)) {
                
                newLine = split[0].split("/")[0] + "/" + newAmount;
                
                for (int i = 1; i < split.length; i++) {
                    
                    split2 = split[i].split("/");
                    
                    if (split2[0].equals(word2)) {
                        
                        newChance = (Float.parseFloat(split[0].split("/")[1]) * Float.parseFloat(split2[1]) + 1) / newAmount;
                        newLine += "//" + split2[0] + "/" + newChance;
                        
                        addNewWord = false;
                        
                    } else {
                        
                        newChance = (Float.parseFloat(split[0].split("/")[1]) * Float.parseFloat(split2[1])) / newAmount;
                        newLine += "//" + split2[0] + "/" + newChance;
                    }
                }
                
                if (addNewWord) {
                    
                    newLine += "//" + word2 + "/" + (1f / newAmount);
                }
                
                newFile.add(newLine);
                
                addNewLine = false;
                
            } else {
                
                newFile.add(line);
            }
        }
        
        if (addNewLine) {
            
            newFile.add(word + "/1//" + word2 + "/1");
        }
        
        String newFileString = String.join("\n", newFile);
        
        FileWriter fileWriter = new FileWriter("markov.txt");
        fileWriter.write(newFileString);
        fileWriter.close();
    }
    
    protected static class MarkovEntry {
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
    }
    
    protected static class MarkovEntryBuilder {
        public final String word;
        public final List<String> followers = new ArrayList<>();
    
        public MarkovEntryBuilder(String word) {
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
