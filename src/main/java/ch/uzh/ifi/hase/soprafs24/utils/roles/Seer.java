package ch.uzh.ifi.hase.soprafs24.utils.roles;

import ch.uzh.ifi.hase.soprafs24.utils.Role;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class Seer extends Role {

    @Autowired
    public Seer() {
        this.roleName = "Seer";
    }

}
