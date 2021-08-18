package com.diamondfire.helpbot.util.textgen;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;

import java.io.*;
import java.util.*;

import static com.diamondfire.helpbot.util.textgen.MarkovManipulation.addWord;

public class CacheData {
    
    public static void cacheData() throws IOException {
        
        File file = ExternalFiles.SAM_QUOTES;
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        FileWriter fileWriter = new FileWriter("markov.txt");
        fileWriter.write("");
        fileWriter.close();
        
        String line;
        while ((line = br.readLine()) != null) {
            
            List<String> splitLine = Arrays.asList(line.split(" "));
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
