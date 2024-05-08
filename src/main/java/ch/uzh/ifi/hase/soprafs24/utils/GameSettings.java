package ch.uzh.ifi.hase.soprafs24.utils;

import javax.persistence.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

@Embeddable
public class GameSettings implements Serializable {
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

    public int getTotalNumberOfRoles() throws IntrospectionException  {
        int totalNumberOfRoles = 0;
        try {
            BeanInfo info = Introspector.getBeanInfo(GameSettings.class, Object.class);

            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method readMethod = pd.getReadMethod();
                try {
                    if (readMethod != null && readMethod.getName().startsWith("getNumberOf")) {
                        GameSettings settings = new GameSettings();
                        Integer value = (Integer) readMethod.invoke(settings);
                        if (value != null) {
                            totalNumberOfRoles += value;
                        }
                    }
                } catch (Exception e){
                    System.err.println("Error while invoking method " + readMethod.getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error during introspection: " + e.getMessage());
        }

        return totalNumberOfRoles;

    }

}


/*    public int getTotalNumberOfRoles() {
        int totalNumberOfRoles = 0;
        try {
            // Get the bean info for the UpdatedGameSettings class
            BeanInfo info = Introspector.getBeanInfo(UpdatedGameSettings.class, Object.class);

            // Loop through the property descriptors
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                // Check if the property's getter method starts with "getNumberOf"
                Method readMethod = pd.getReadMethod();
                if (readMethod != null && readMethod.getName().startsWith("getNumberOf")) {
                    try {
                        // Invoke the getter method on an instance of UpdatedGameSettings
                        UpdatedGameSettings settings = new UpdatedGameSettings();
                        Integer value = (Integer) readMethod.invoke(settings);

                        // Add the value to the total if it's not null
                        if (value != null) {
                            totalNumberOfRoles += value;
                        }
                    } catch (Exception e) {
                        // Handle potential exceptions when invoking the getter method
                        System.err.println("Error while invoking method " + readMethod.getName() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during introspection: " + e.getMessage());
        }

        return totalNumberOfRoles;
    }*/