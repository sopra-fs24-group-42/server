package ch.uzh.ifi.hase.soprafs24.utils.roles;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.utils.Role;

@Component
public class Werewolf extends Role{

    private ServiceProvider serviceProvider;

    @Autowired
    public Werewolf(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void doNightAction(){
        serviceProvider.getPlayerService().killPlayer(this.getSelection());
    }

    @Override
    public void doVotingAction(){

    }

    @Override
    public void doDayAction(){

    }
}
