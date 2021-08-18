package com.diamondfire.helpbot.sys.message.filter.filters;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.sys.message.filter.*;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.*;

import java.nio.file.Files;
import java.util.*;

public class SwearFilter extends ChatFilter {
    
    private static final HashSet<String> EQUAL_SWEARS = new HashSet<>();
    private static final String[] PREFIX_SWEARS;
    private static final String[] SUFFIX_SWEARS;
    private static final String[] PART_SWEARS;
    private static final HashSet<String> BLOCKED_MESSAGES = new HashSet<>();
    
    static {
        try {
            
            JsonObject object = ExternalFiles.FILTER.parseJson();
            
            Collections.addAll(EQUAL_SWEARS, Util.fromJsonArray(object.getAsJsonArray("equal")));
            PREFIX_SWEARS = Util.fromJsonArray(object.getAsJsonArray("prefix"));
            SUFFIX_SWEARS = Util.fromJsonArray(object.getAsJsonArray("suffix"));
            PART_SWEARS = Util.fromJsonArray(object.getAsJsonArray("part"));
            
            BLOCKED_MESSAGES.addAll(EQUAL_SWEARS);
            BLOCKED_MESSAGES.addAll(Arrays.asList(PREFIX_SWEARS));
            BLOCKED_MESSAGES.addAll(Arrays.asList(SUFFIX_SWEARS));
            BLOCKED_MESSAGES.addAll(Arrays.asList(PART_SWEARS));
            
        } catch (Exception e) {
            throw new IllegalStateException("Malformed swear filter file!");
        }
    }
    
    @Override
    public boolean filterString(String message, FilterData data) {
        message = message.toLowerCase();
        String[] components = message.split("[.,?!]? "); // For future reference: [^.,!? ]+
        
        for (String component : components) {
            String implicit = getImplicit(component);
            
            if (EQUAL_SWEARS.contains(implicit)) {
                return true;
            }
            for (String match : PREFIX_SWEARS) {
                if (implicit.startsWith(match)) {
                    return true;
                }
            }
            for (String match : SUFFIX_SWEARS) {
                if (implicit.endsWith(match)) {
                    return true;
                }
            }
            for (String match : PART_SWEARS) {
                if (implicit.contains(match)) {
                    return true;
                }
            }
        }
        
        // This is fairly easy to bypass, but still good to have
        String concatenated = getImplicit(message.replace(" ", ""));

        return BLOCKED_MESSAGES.contains(concatenated);
    }
    
    private static final HashMap<Character, Character> CHARACTER_MAP;
    
    static {
        CHARACTER_MAP = new HashMap<>();
        
        // List of possible characters the filter will read.
        char[][] registeredChars = new char[][]{
                {'a', '4', '@', 'ａ', 'ⓐ', 'a', 'ᵃ', 'ₐ', 'ᴬ', 'á', 'à', 'ă', 'ắ', 'ằ', 'ẵ', 'ẳ', 'â', 'ấ', 'ầ', 'ẫ', 'ẩ', 'ǎ', 'å', 'ǻ', 'ä', 'ǟ', 'ã', 'ȧ', 'ǡ', 'ą', 'ā', 'ả', 'ȁ', 'ȃ', 'ạ', 'ặ', 'ậ', 'ḁ', 'ꜹ', 'ꜻ', 'ꜽ', 'ẚ', 'ᴀ', 'ⱥ', 'ᶏ', 'ɐ', 'ɑ', 'ᶐ', 'ɒ'},
                {'b', 'ｂ', 'ⓑ', 'b', 'ḃ', 'ḅ', 'ḇ', 'ʙ', 'ƀ', 'ᵬ', 'ᶀ', 'ɓ', 'ƃ'},
                {'c', 'ｃ', 'ⅽ', 'ⓒ', 'c', 'ℂ', 'ℭ', 'ᶜ', 'ć', 'ĉ', 'č', 'ċ', 'ç', 'ḉ', 'ᴄ', 'ȼ', 'ꞔ', 'Ꞔ', 'ƈ', 'ɕ', 'ↄ', 'ꜿ'},
                {'d', 'ｄ', 'ⅾ', 'ⅆ', 'ⓓ', 'd', 'ⅅ', 'ᵈ', 'ᴰ', 'ď', 'ḋ', 'ḑ', 'đ', 'ḍ', 'ḓ', 'ḏ', 'ð', 'ᴅ', 'ᴆ', 'ᵭ', 'ᶁ', 'ɖ', 'ɗ', 'ᶑ', 'ƌ', 'ȡ', 'ꝱ'},
                {'e', '3', 'ｅ', 'ℯ', 'ⓔ', 'e', 'ℰ', 'ᵉ', 'ₑ', 'ᴱ', 'é', 'è', 'ĕ', 'ê', 'ế', 'ề', 'ễ', 'ể', 'ě', 'ë', 'ẽ', 'ė', 'ȩ', 'ḝ', 'ę', 'ē', 'ḗ', 'ḕ', 'ẻ', 'ȅ', 'ȇ', 'ẹ', 'ệ', 'ḙ', 'ḛ', 'ᴇ', 'ɇ', 'ᶒ', 'ⱸ', 'ǝ', 'ᴲ', 'ⱻ', 'ə', 'ɛ', 'ℇ', 'ᵋ', 'ᶓ', 'ɘ', 'ɜ', 'ᶟ', 'ᶔ', 'ᴈ', 'ᵌ'},
                {'f', 'ｆ', 'ⓕ', 'f', 'ᶠ', 'ḟ', 'ꝼ', 'ﬀ', 'ʩ', 'ꜰ', 'ᵮ', 'ᶂ', 'ƒ', 'ⅎ', 'ꟻ'},
                {'g', 'ｇ', 'ℊ', 'ⓖ', 'g', 'ᵍ', 'ᴳ', 'ǵ', 'ğ', 'ĝ', 'ǧ', 'ġ', 'ģ', 'ḡ', 'ɡ', 'ᶢ', 'ɢ', 'ǥ', 'ᶃ', 'ɠ', 'ʛ'},
                {'h', 'ｈ', 'ℎ', 'ⓗ', 'h', 'ℋ', 'ℌ', 'ℍ', 'ʰ', 'ᴴ', 'ĥ', 'ȟ', 'ḧ', 'ḣ', 'ḩ', 'ħ', 'ℏ', 'ḥ', 'ḫ', 'ẖ', 'ʜ', 'ƕ', 'ꞕ', 'ɦ', 'ⱨ', 'ꜧ'},
                {'i', '1', '!', 'ｉ', 'ⅰ', 'ℹ', 'ⓘ', 'i', 'ⁱ', 'ᵢ', 'ᴵ', 'í', 'ì', 'ĭ', 'î', 'ǐ', 'ï', 'ḯ', 'ĩ', '̇', 'į', 'ī', 'ỉ', 'ȉ', 'ȋ', 'ị', 'ḭ', 'ı', 'ɪ', 'ᶦ', 'ꟾ', 'ᴉ', 'ɨ', 'ᶖ', 'ɩ'},
                {'j', 'ｊ', 'ⓙ', 'j', 'ĵ', 'ǰ', 'ȷ', 'ɉ', 'ʝ'},
                {'k', 'ｋ', 'ⓚ', 'k', 'ᵏ', 'ᴷ', 'ḱ', 'ǩ', 'ķ', 'ḳ', 'ḵ', 'ᴋ', 'ĸ', 'ᶄ', 'ƙ', 'ⱪ', 'ʞ', 'Ʞ'},
                {'l', '|', 'ｌ', 'ⅼ', 'ⓛ', 'l', 'ĺ', 'ľ', 'ļ', 'ł', 'ḷ', 'ḹ', 'ḽ', 'ḻ', 'ŀ', 'ỻ', 'ʟ', 'ƚ', 'ɫ', 'ᶅ', 'ɭ', 'ꞁ'},
                {'m', 'ｍ', 'ⅿ', 'ⓜ', 'm', 'ℳ', 'ᵐ', 'ᴹ', 'ḿ', 'ṁ', 'ṃ', 'ᴍ', 'ᶆ', 'ɱ', 'ᶬ'},
                {'n', 'ｎ', 'ⓝ', 'n', 'ℕ', 'ⁿ', 'ᴺ', 'ń', 'ǹ', 'ň', 'ñ', 'ṅ', 'ņ', 'ṇ', 'ṋ', 'ṉ', '№', 'ɴ', 'ᶰ', 'ᴻ', 'ᴎ', 'ɲ', 'ᶮ', 'ƞ', 'ᶇ', 'ɳ', 'ᶯ', 'ȵ', 'ꝴ', 'ŋ'},
                {'o', '0', 'ｏ', 'ⓞ', 'ó', 'ò', 'ŏ', 'ô', 'ố', 'ồ', 'ỗ', 'ổ', 'ǒ', 'ö', 'ȫ', 'ő', 'õ', 'ṍ', 'ṏ', 'ȭ', 'ȯ', 'ȱ', 'ø', 'ǿ', 'ǫ', 'ǭ', 'ō', 'ṓ', 'ṑ', 'ỏ', 'ȍ', 'ȏ', 'ơ', 'ớ', 'ờ', 'ỡ', 'ở', 'ợ', 'ọ', 'ộ', 'ᴏ', 'ᴑ', 'ɵ'},
                {'p', 'ｐ', 'ⓟ', 'p', 'ℙ', 'ᵖ', 'ᴾ', 'ṕ', 'ṗ', 'ᴘ', 'ᵽ', 'ꝑ', 'ᵱ', 'ᶈ', 'ƥ', 'ꝕ', 'ꟼ'},
                {'q', 'ｑ', 'ⓠ', 'q', 'ʠ', 'ɋ'},
                {'r', 'ｒ', 'ⓡ', 'r', 'ℛ', 'ℜ', 'ℝ', 'ʳ', 'ᵣ', 'ᴿ', 'ŕ', 'ř', 'ṙ', 'ŗ', 'ȑ', 'ȓ', 'ṛ', 'ṝ', 'ṟ', 'ʀ', 'ᴙ', 'ɍ', 'ᵲ', 'ɹ', 'ʴ', 'ᴚ', 'ɺ', 'ᶉ', 'ɼ', 'ɽ', 'ɾ', 'ᵳ'},
                {'s', '$', 'ｓ', 'ⓢ', 's', 'ˢ', 'ś', 'ṥ', 'ŝ', 'š', 'ṧ', 'ṡ', 'ş', 'ꞩ', 'ṣ', 'ṩ', 'ș', 'ꜱ', 'ᵴ', 'ᶊ', 'ʂ', 'ȿ'},
                {'t', 'ｔ', 'ⓣ', 't', 'ᵗ', 'ᵀ', 'ť', 'ẗ', 'ṫ', 'ţ', 'ṭ', 'ț', 'ṱ', 'ṯ', 'ᴛ', 'ƫ', 'ƭ', 'ʈ', 'ȶ'},
                {'u', 'ｕ', 'ⓤ', 'u', 'ᵘ', 'ᵤ', 'ᵁ', 'ú', 'ù', 'ŭ', 'û', 'ǔ', 'ů', 'ü', 'ǘ', 'ǜ', 'ǚ', 'ǖ', 'ű', 'ũ', 'ṹ', 'ų', 'ū', 'ṻ', '◌', 'ᷰ', 'ủ', 'ȕ', 'ȗ', 'ư', 'ứ', 'ừ', 'ữ', 'ử', 'ự', 'ụ', 'ṳ', 'ṷ', 'ṵ', 'ᴜ', 'ᶸ', 'ʉ', 'ᶙ', 'ʊ'},
                {'v', 'ｖ', 'ⅴ', 'ⓥ', 'v', 'ᵛ', 'ᵥ', 'ⱽ', 'ṽ', 'ṿ', 'ᴠ', 'ᶌ', 'ⱱ'},
                {'w', 'ｗ', 'ⓦ', 'w', 'ẃ', 'ẁ', 'ŵ', 'ẘ', 'ẅ', 'ẇ', 'ẉ', 'ᴡ'},
                {'x', 'ｘ', 'ⅹ', 'ⓧ', 'x', 'ˣ', 'ₓ', 'ẍ', 'ẋ', 'ᶍ'},
                {'y', 'ｙ', 'ⓨ', 'y', 'ý', 'ỳ', 'ŷ', 'ẙ', 'ÿ', 'ỹ', 'ẏ', 'ȳ', 'ỷ', 'ỵ', 'ʏ', 'ɏ', 'ƴ', 'ỿ'},
                {'z', 'ｚ', 'ⓩ', 'z', 'ź', 'ẑ', 'ž', 'ż', 'ẓ', 'ẕ', 'ᴢ', 'ƶ', 'ᵶ', 'ᶎ', 'Ᶎ', 'ȥ', 'ʐ', 'ɀ'},
                
                
                {'2'},
                {'3'},
                {'5'},
                {'6'},
                {'7'},
                {'8'},
                {'9'},
        };
        
        // Register the characters.
        for (char[] charVariations : registeredChars) {
            for (char charVariation : charVariations) {
                CHARACTER_MAP.put(charVariation, charVariations[0]);
            }
        }
    }
    
    public static String[] getComponents(String message) {
        message = message.toLowerCase();
        return message.split("[.,?!]? ");
    }
    
    /**
     * Returns the implied word based on a written component
     * ex. "b4na.ná" returns "banana"
     */
    public static String getImplicit(String component) {
        StringBuilder implicit = new StringBuilder();
        for (char written : component.toCharArray()) {
            if (CHARACTER_MAP.containsKey(written)) {
                implicit.append(CHARACTER_MAP.get(written));
            }
        }
        return implicit.toString();
    }
}
