package ch.uzh.ifi.hase.soprafs24.utils;

import javax.persistence.*;
import java.util.ArrayList;

@Embeddable
public class GameSettings {
    private int numberOfWerewolves;
    private int numberOfVillagers;
    private int numberOfProtectors;
    private int numberOfSeers;
    private int numberOfSheriffs;
    private int numberOfMayors;
    private int numberOfJesters;
    private int numberOfSacrifices;
    private int numberOfAmours;
    private int numberOfHunters;
    private int numberOfSwappers;

    public GameSettings() {
        numberOfWerewolves = 1;
        numberOfVillagers = 1;
        numberOfProtectors = 0;
        numberOfSeers = 1;
        numberOfSheriffs = 0;
        numberOfMayors = 0;
        numberOfJesters = 0;
        numberOfSacrifices = 0;
        numberOfAmours = 0;
        numberOfHunters = 0;
        numberOfSwappers = 0;
    }

    public int getNumberOfWerewolves() {
        return numberOfWerewolves;
    }

    public void setNumberOfWerewolves(int numberOfWerewolves) {
        this.numberOfWerewolves = numberOfWerewolves;
    }

    public int getNumberOfVillagers() {
        return numberOfVillagers;
    }

    public void setNumberOfVillagers(int numberOfVillagers) {
        this.numberOfVillagers = numberOfVillagers;
    }

    public int getNumberOfProtectors() {
        return numberOfProtectors;
    }

    public void setNumberOfProtectors(int numberOfProtectors) {
        this.numberOfProtectors = numberOfProtectors;
    }

    public int getNumberOfSeers() {
        return numberOfSeers;
    }

    public void setNumberOfSeers(int numberOfSeers) {
        this.numberOfSeers = numberOfSeers;
    }

    public int getNumberOfSheriffs() {
        return numberOfSheriffs;
    }

    public void setNumberOfSheriffs(int numberOfSheriffs) {
        this.numberOfSheriffs = numberOfSheriffs;
    }

    public int getNumberOfMayors() {
        return numberOfMayors;
    }

    public void setNumberOfMayors(int numberOfMayors) {
        this.numberOfMayors = numberOfMayors;
    }

    public int getNumberOfJesters() {
        return numberOfJesters;
    }

    public void setNumberOfJesters(int numberOfJesters) {
        this.numberOfJesters = numberOfJesters;
    }

    public int getNumberOfSacrifices() {
        return numberOfSacrifices;
    }

    public void setNumberOfSacrifices(int numberOfSacrifices) {
        this.numberOfSacrifices = numberOfSacrifices;
    }

    public int getNumberOfAmours() {
        return numberOfAmours;
    }

    public void setNumberOfAmours(int numberOfAmours) {
        this.numberOfAmours = numberOfAmours;
    }

    public int getNumberOfHunters() {
        return numberOfHunters;
    }

    public void setNumberOfHunters(int numberOfHunters) {
        this.numberOfHunters = numberOfHunters;
    }

    public int getNumberOfSwappers() {
        return numberOfSwappers;
    }

    public void setNumberOfSwappers(int numberOfSwappers) {
        this.numberOfSwappers = numberOfSwappers;
    }

    public ArrayList<String> RoleList() {
        ArrayList<String> roles = new ArrayList<>();

        for (int i = 0; i < numberOfWerewolves; i++) {
            roles.add("Werewolf");
        }
        for (int i = 0; i < numberOfVillagers; i++) {
            roles.add("Villager");
        }
        for (int i = 0; i < numberOfProtectors; i++) {
            roles.add("Protector");
        }
        for (int i = 0; i < numberOfSeers; i++) {
            roles.add("Seer");
        }
        for (int i = 0; i < numberOfSheriffs; i++) {
            roles.add("Sheriff");
        }
        for (int i = 0; i < numberOfMayors; i++) {
            roles.add("Mayor");
        }
        for (int i = 0; i < numberOfJesters; i++) {
            roles.add("Jester");
        }
        for (int i = 0; i < numberOfSacrifices; i++) {
            roles.add("Sacrifice");
        }
        for (int i = 0; i < numberOfAmours; i++) {
            roles.add("Amour");
        }
        for (int i = 0; i < numberOfHunters; i++) {
            roles.add("Hunter");
        }
        for (int i = 0; i < numberOfSwappers; i++) {
            roles.add("Swapper");
        }

        return roles;
    }

}

// add here the method to convert the game setting to the dictionary

/*import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

public class RoleManager {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("your.package.name"); // specify your package name
        Set<Class<? extends Role>> roleClasses = reflections.getSubTypesOf(Role.class);

        List<Role> roles = new ArrayList<>();
        for (Class<? extends Role> roleClass : roleClasses) {
            try {
                roles.add(roleClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Displaying descriptions and actions for each role
        for (Role role : roles) {
            System.out.println(role.getDescription());
            role.performAction();
        }
    }
}
*/
