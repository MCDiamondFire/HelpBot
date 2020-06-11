package com.diamondfire.helpbot.util;


public class JaroWinkler {

    final static double DEFAULT_SCALING_FACTOR = 0.1;  // This is the default scaling factor Winkler used.

    public static double score(String first, String second) {
        double jaro = scoreT(first, second);

        // The Jaro–Winkler distance uses a prefix scale which gives more favorable ratings
        // to strings that match from the beginning for a set prefix length.
        return jaro + (DEFAULT_SCALING_FACTOR * commonPrefixLength(first, second) * (1.0 - jaro));
    }

    private static int commonPrefixLength(String first, String second) {
        String shorter;
        String longer;

        // Determine which string is longer.
        if (first.length() > second.length()) {
            longer = first.toLowerCase();
            shorter = second.toLowerCase();
        } else {
            longer = second.toLowerCase();
            shorter = first.toLowerCase();
        }

        int result = 0;

        // Iterate through the shorter string.
        for (int i = 0; i < shorter.length(); i++) {
            if (shorter.charAt(i) != longer.charAt(i)) {
                break;
            }
            result++;
        }

        // Limit the result to 4.
        return Math.min(result, 4);
    }

    private static double scoreT(String first, String second) {
        String shorter;
        String longer;

        // Determine which String is longer.
        if (first.length() > second.length()) {
            longer = first.toLowerCase();
            shorter = second.toLowerCase();
        } else {
            longer = second.toLowerCase();
            shorter = first.toLowerCase();
        }

        // Calculate the half length() distance of the shorter String.
        int halflength = (shorter.length() / 2) + 1;

        // Find the set of matching characters between the shorter and longer strings. Note that
        // the set of matching characters may be different depending on the order of the strings.
        String m1 = getSetOfMatchingCharacterWithin(shorter, longer, halflength);
        String m2 = getSetOfMatchingCharacterWithin(longer, shorter, halflength);


        // If one or both of the sets of common characters is empty, then
        // there is no similarity between the two strings.
        if (m1.length() == 0 || m2.length() == 0) return 0.0;

        // If the set of common characters is not the same size, then
        // there is no similarity between the two strings, either.
        if (m1.length() != m2.length()) return 0.0;

        // Calculate the distance.
        return (m1.length() / ((double) shorter.length()) +
                m2.length() / ((double) longer.length()) +
                (m1.length() - transpositions(m1, m2)) / ((double) m1.length())) / 3.0;

    }

    private static String getSetOfMatchingCharacterWithin(String first, String second, int limit) {

        StringBuilder common = new StringBuilder();
        StringBuilder copy = new StringBuilder(second);
        for (int i = 0; i < first.length(); i++) {
            char ch = first.charAt(i);
            boolean found = false;

            // See if the character is within the limit positions away from the original position of that character.
            for (int j = Math.max(0, i - limit); !found && j < Math.min(i + limit, second.length()); j++) {
                if (copy.charAt(j) == ch) {
                    found = true;
                    common.append(ch);
                    copy.setCharAt(j, '*');
                }
            }
        }
        return common.toString();
    }

    private static int transpositions(String first, String second) {
        int transpositions = 0;
        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) {
                transpositions++;
            }
        }
        transpositions /= 2;
        return transpositions;
    }
}
