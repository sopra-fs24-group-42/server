package ch.uzh.ifi.hase.soprafs24.websocket.dto;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class UpdatedGameSettings {
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

    public int getTotalNumberOfRoles() {
        int totalNumberOfRoles = 0;
        BeanInfo info = Introspector.getBeanInfo( UpdatedGameSettings.class, Object.class );
        for ( PropertyDescriptor pd : info.getPropertyDescriptors() ){
            if(pd.startsWith("getNumberOf")){
                totalNumberOfRoles += pd;
            }
        }

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
