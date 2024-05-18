package ch.uzh.ifi.hase.soprafs24.utils;

import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import javax.persistence.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


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

    public int getTotalNumberOfRoles() {
        int totalNumberOfRoles = 0;
        try {
            // return the name of the class 'GameSettings'
            Class classType = this.getClass();

            // get all the methods of the class
            Method[] methods = classType.getDeclaredMethods();

            // iterate ovet each in the list
            for (Method method : methods) {
                // filter on getters with the name of the method that starts with 'getNumberOf'
                if (method.getName().startsWith("getNumberOf")) {
                    // value is what is returned by a getter
                    Object value = method.invoke(this, (Object[]) null);
                    // sum up all the getters
                    totalNumberOfRoles += (int) value;
                }
            }
        } catch (Exception e) {
            // have to catch error in case there is IllegalAccessException (thrying to access the class, method)
            // or InvocationTargetException (throwm upon trying to access the method or constructor)
            System.err.println("Error while invoking methods to get the total number of roles");
        }
        return totalNumberOfRoles;
    }
}