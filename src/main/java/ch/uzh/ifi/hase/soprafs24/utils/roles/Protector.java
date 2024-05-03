package ch.uzh.ifi.hase.soprafs24.utils.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.utils.Role;

@Component
public class Protector extends Role {
    private ServiceProvider serviceProvider;

    @Autowired
    public Protector(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void doNightAction() {
        serviceProvider.getPlayerService().protectPlayer(this.getSelection());
    }
    
}
