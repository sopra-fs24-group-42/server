package ch.uzh.ifi.hase.soprafs24.utils.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.utils.Role;

@Component
public class Sacrifice extends Role {

    private ServiceProvider serviceProvider;

    @Autowired
    public Sacrifice(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.roleName = "Sacrifice";
    }

    @Override
    public void doNightAction() {
        serviceProvider.getPlayerService().sacrificePlayer(this.getUsername());
        serviceProvider.getPlayerService().sacrificePlayer(this.getSelection());
    } 
}