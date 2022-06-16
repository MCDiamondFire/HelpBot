package com.diamondfire.helpbot.util.textgen;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SamQuotes {
    protected static MarkovManipulation markov = new MarkovManipulation();
    protected static List<String> quotes = List.of();
    
    public static void load() throws IOException {
        load(Files.readAllLines(ExternalFiles.SAM_QUOTES));
    }
    
    public static void load(List<String> lines) {
        quotes = lines;
        markov.load(lines);
    }
    
    public static void add(String quote) {
        quotes.add(quote);
    }
    
    public static void save() throws IOException {
        Files.writeString(ExternalFiles.SAM_QUOTES, String.join("\n", quotes));
    }
    
    public static String getNextWord(String word) {
        return markov.getNextWord(word);
    }
    
    public static String generateFull(int wordLimit) {
        return markov.generateFull(wordLimit);
    }
    
    public static String generateFull(int wordLimit, String startingWord) {
        return markov.generateFull(wordLimit, startingWord);
    }
}
