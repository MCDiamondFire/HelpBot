package com.diamondfire.helpbot.util.textgen;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static com.diamondfire.helpbot.util.textgen.MarkovManipulation.addWord;

public class CacheData {
    
    public static void cacheData() throws IOException {
        long time = System.nanoTime();
        
        List<String> lines = Files.readAllLines(ExternalFiles.SAM_QUOTES);
        
//        FileWriter fileWriter = new FileWriter("markov.txt");
//        fileWriter.write("");
//        fileWriter.close();
        
        Map<String, MarkovManipulation.MarkovEntryBuilder> builders = new HashMap<>();
        
        for (String line : lines) {
            String[] splitLine = line.split(" ");
            for (int i = 0; i < splitLine.length; i++) {
                String word = splitLine[i];
                MarkovManipulation.MarkovEntryBuilder builder = builders.computeIfAbsent(word, MarkovManipulation.MarkovEntryBuilder::new);
                
                int nextIndex = i + 1;
                String next = nextIndex >= splitLine.length ? "." : splitLine[nextIndex];
                builder.followers.add(next);
            }
        }
        
        System.out.println(System.nanoTime() - time);
    }
}
