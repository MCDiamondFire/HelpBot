package com.diamondfire.helpbot.util.textgen;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.diamondfire.helpbot.util.textgen.MarkovManipulation.addWord;

public class CacheData {
    
    public static void CacheData() throws IOException {
        
        File file = new File("samquotes.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        FileWriter fileWriter = new FileWriter("markov.txt");
        fileWriter.write("");
        fileWriter.close();
        
        String line;
        while ((line = br.readLine()) != null) {
            
            ArrayList<String> splitLine = new ArrayList<>();
            splitLine.addAll(Arrays.asList(line.split(" ")));
            
            for (int i = 0; i < splitLine.size(); i++) {
                
                if (i == splitLine.size() - 1) {
                    
                    addWord(splitLine.get(i), ".");
                    
                } else {
                    
                    addWord(splitLine.get(i), splitLine.get(i + 1));
                }
            }
        }
    }
}
