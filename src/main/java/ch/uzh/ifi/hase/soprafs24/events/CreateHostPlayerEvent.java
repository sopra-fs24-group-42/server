package ch.uzh.ifi.hase.soprafs24.events;

import org.springframework.context.ApplicationEvent;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

public class CreateHostPlayerEvent extends ApplicationEvent {
    private final Player player;

    public CreateHostPlayerEvent(Object source, Player player) {
        super(source);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
