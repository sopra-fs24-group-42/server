package ch.uzh.ifi.hase.soprafs24.utils;

import java.security.SecureRandom;

public class LobbyCodeGenerator {
    private static final int Length = 5; // code lenght
    private static final String CHARACTERS = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";

    // object of the java class SecureRandom
    // to generate cryptographically strong random number
    private static final SecureRandom random = new SecureRandom();

    // logic for code generation
    public static String generateLobbyCode() {
        StringBuilder code = new StringBuilder(); // instance to construct the string
        for (int i = 0; i < Length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length()); // selects random position
            char randomChar = CHARACTERS.charAt(randomIndex); // gets the char at the position
            code.append(randomChar); // append with string builder to create a code
        }
        return code.toString();
    }

}
