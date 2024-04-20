package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.context.event.EventListener;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final RepositoryProvider repositoryProvider;

    @Autowired
    public PlayerService(RepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    public void checkIfUserExists(Player playerToBeCreated) {
        Player userByUsername = repositoryProvider.getPlayerRepository().findByUsername(playerToBeCreated.getUsername());

        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Therefore, the player could not be created!");
        }
    }

    public Long getLobbyIdFromPlayerByUsername(String username) {
        Player player = repositoryProvider.getPlayerRepository().findByUsername(username);
        return player.getLobbyId();
    }

    public void assignRoles(Long lobbyId) {
        try {
            // Fetch players and roles
            List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);
            ArrayList<String> roles = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId).getGameSettings().RoleList();
    
            // Check if there are enough roles and players
            if (players == null || roles == null || players.size() < roles.size()) {
                throw new IllegalArgumentException("Not enough players or roles available for assignment.");
            }
    
            // Shuffle roles
            Collections.shuffle(roles);
    
            // Assign roles to players
            for (int i = 0; i < roles.size(); i++) {
                players.get(i).setRoleName(roles.get(i));
            }
        } catch (Exception e) {
            // Log the exception and possibly throw it to be handled by a higher-level function
            System.err.println("An error occurred during role assignment: " + e.getMessage());
            throw e;
        }

    }
}
