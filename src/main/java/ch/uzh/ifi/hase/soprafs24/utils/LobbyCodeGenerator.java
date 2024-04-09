package ch.uzh.ifi.hase.soprafs24.utils;

import java.security.SecureRandom;

// add the description of the class
public class LobbyCodeGenerator {
    private static final int Length = 5;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateLobbyCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < Length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            code.append(randomChar);
        }
        return code.toString();
    }

}
