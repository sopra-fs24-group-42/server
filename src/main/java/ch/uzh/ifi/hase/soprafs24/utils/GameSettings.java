package ch.uzh.ifi.hase.soprafs24.utils;

import javax.persistence.*;

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
        numberOfProtectors = 1;
        numberOfSeers = 1;
        numberOfSheriffs = 1;
        numberOfMayors = 1;
        numberOfJesters = 1;
        numberOfSacrifices = 1;
        numberOfAmours = 1;
        numberOfHunters = 1;
        numberOfSwappers = 1;
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
