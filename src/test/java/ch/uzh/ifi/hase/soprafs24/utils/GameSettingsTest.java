package ch.uzh.ifi.hase.soprafs24.utils;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameSettingsTest {

    private GameSettings gameSettings;

    @BeforeEach
    public void setUp() {
        gameSettings = new GameSettings();
        // Set up with some reasonable default values.
        gameSettings.setNumberOfWerewolves(2);
        gameSettings.setNumberOfVillagers( 3);
        gameSettings.setNumberOfProtectors(1);
        gameSettings.setNumberOfSeers(1);
        gameSettings.setNumberOfSheriffs(1);
        gameSettings.setNumberOfMayors(1);
        gameSettings.setNumberOfJesters(1);
        gameSettings.setNumberOfSacrifices(1);
        gameSettings.setNumberOfAmours(1);
        gameSettings.setNumberOfHunters(1);
        gameSettings.setNumberOfSwappers(1);
    }

    @Test
    public void testRoleListSize() {
        ArrayList<String> roles = gameSettings.RoleList();
        int expectedSize = 14; // Total roles added up
        assertEquals(expectedSize, roles.size());
    }

    @Test
    public void testRoleListContents() {
        ArrayList<String> roles = gameSettings.RoleList();

        int werewolfCount = Collections.frequency(roles, "Werewolf");
        assertEquals(2, werewolfCount);

        int villagerCount = Collections.frequency(roles, "Villager");
        assertEquals(3, villagerCount);

        int protectorCount = Collections.frequency(roles, "Protector");
        assertEquals(1, protectorCount);

        int seerCount = Collections.frequency(roles, "Seer");
        assertEquals(1, seerCount);
        
        int sheriffCount = Collections.frequency(roles, "Sheriff");
        assertEquals(1, sheriffCount);

        int mayorCount = Collections.frequency(roles, "Mayor");
        assertEquals(1, mayorCount);
        
        int jesrterCount = Collections.frequency(roles, "Jester");
        assertEquals(1, jesrterCount);
        
        int sacrificeCount = Collections.frequency(roles, "Sacrifice");
        assertEquals(1, sacrificeCount);
        
        int amourCount = Collections.frequency(roles, "Amour");
        assertEquals(1, amourCount);
        
        int hunterCount = Collections.frequency(roles, "Hunter");
        assertEquals(1, hunterCount);
        
        int swapperCount = Collections.frequency(roles, "Swapper");
        assertEquals(1, swapperCount);
    }

    @Test
    public void testRoleListWithZeroRoles() {
        gameSettings.setNumberOfWerewolves(0);
        gameSettings.setNumberOfVillagers(0);
        gameSettings.setNumberOfProtectors(0);
        gameSettings.setNumberOfSeers(0);
        gameSettings.setNumberOfSheriffs(0);
        gameSettings.setNumberOfMayors(0);
        gameSettings.setNumberOfJesters(0);
        gameSettings.setNumberOfSacrifices(0);
        gameSettings.setNumberOfAmours(0);
        gameSettings.setNumberOfHunters(0);
        gameSettings.setNumberOfSwappers(0);

        ArrayList<String> roles = gameSettings.RoleList();
        assertTrue(roles.isEmpty());
    }
}
