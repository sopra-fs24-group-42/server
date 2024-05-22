package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private Random rand = new Random();

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
        if(player == null){log.info("No player with such username"); return -1L;}
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

            repositoryProvider.getPlayerRepository().saveAll(players); //if roleassignemt breaks remove this!
        } catch (Exception e) {
            // Log the exception and possibly throw it to be handled by a higher-level function
            log.info("An error occurred during role assignment: " + e.getMessage());
            throw e;
        }
    }

    public void setPlayerReady(String username, GameState clientGameState) {
        Player readyPlayer = repositoryProvider.getPlayerRepository().findByUsername(username);
        if(readyPlayer == null){log.info("No player with such username"); return;}
        Lobby lobbyOfReadyPlayer = repositoryProvider.getLobbyRepository().findByLobbyCode(readyPlayer.getLobbyCode());

        //check if client and server are in same gameState
        if (lobbyOfReadyPlayer.getGameState() != clientGameState) {
            log.info("client of {} is not in the same gameState as the server!", username);
            return;
        }

        readyPlayer.setIsReady(Boolean.TRUE);
        repositoryProvider.getPlayerRepository().save(readyPlayer);

        log.info("User {} is set ready in lobby {}", readyPlayer.getUsername(), lobbyOfReadyPlayer.getLobbyId());
    }

    public boolean areAllPlayersReady(Long lobbyId) {

        List<Player> readyPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsReady(lobbyId, Boolean.TRUE);
        Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);

        int numOfPlayers = lobby.getNumberOfPlayers();
        int countNotReady = numOfPlayers - readyPlayers.size();

        log.info("{} Players are not ready yet", countNotReady);

        return countNotReady == 0;
    }

    public void setPlayersNotReady (Long lobbyId) {
        List<Player> alivePlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsAlive(lobbyId, Boolean.TRUE);

        for (Player player : alivePlayers) {
            player.setIsReady(Boolean.FALSE);
        }

        repositoryProvider.getPlayerRepository().saveAll(alivePlayers);
    }

    public void resetIsKilled (Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for(Player player : players) {
            player.setIsKilled(Boolean.FALSE);
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
    }

    public void resetVotes(Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for(Player player : players) {
            player.setNumberOfVotes(0);
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
    }

    public void voteForPlayer(String selectedPlayerName) {
        Player votedPlayer = repositoryProvider.getPlayerRepository().findByUsername(selectedPlayerName);
        votedPlayer.setNumberOfVotes(votedPlayer.getNumberOfVotes() + 1);
        
        repositoryProvider.getPlayerRepository().save(votedPlayer);
    }

    public void killPlayer (String unsername) {
        Player playerToKill = repositoryProvider.getPlayerRepository().findByUsername(unsername);
        playerToKill.setIsKilled(Boolean.TRUE);
        
        repositoryProvider.getPlayerRepository().save(playerToKill);
    }

    public void sacrificePlayer (String username) {
        Player playerToSacrifice = repositoryProvider.getPlayerRepository().findByUsername(username);
        playerToSacrifice.setIsSacrificed(Boolean.TRUE);

        repositoryProvider.getPlayerRepository().save(playerToSacrifice);
    }

    public void protectPlayer (String username) {
        Player playerToProtect = repositoryProvider.getPlayerRepository().findByUsername(username);
        playerToProtect.setIsProtected(Boolean.TRUE);

        repositoryProvider.getPlayerRepository().save(playerToProtect);
    }

    public boolean playersLobbyEqual (String usernameOne, String usernameTwo) {
        return getLobbyIdFromPlayerByUsername(usernameOne).equals(getLobbyIdFromPlayerByUsername(usernameTwo));
    }

    @Transactional
    public void resetPlayersByLobbyId (Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for (Player player : players) {
            player.setRoleName(null);
            player.setIsAlive(Boolean.TRUE);
            player.setIsProtected(Boolean.FALSE);
            player.setIsKilled(Boolean.FALSE);
            player.setIsSacrificed(Boolean.FALSE);
            player.setIsReady(Boolean.FALSE);
            player.setNumberOfVotes(0);
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
        repositoryProvider.getPlayerRepository().flush();
    }

    public void updateLeaderboardWerewolfWin (Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for (Player player : players) {
            if (player.getRoleName().equals("Werewolf")) {
                player.setNumberOfWerewolfWins(player.getNumberOfWerewolfWins() + 1);
                player.setNumberOfWins(player.getNumberOfWins() + 1);
            }
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
    }

    public void updateLeaderboardVillagerWin (Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for (Player player : players) {
            if (!player.getRoleName().equals("Werewolf")) {
                player.setNumberOfVillagerWins(player.getNumberOfVillagerWins() + 1);
                player.setNumberOfWins(player.getNumberOfWins() + 1);
            }
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
    }

    public List<Player> getTopPlayers (int playerLimit) {
        List<Player> topPlayers = repositoryProvider.getPlayerRepository().findAll();

        if (topPlayers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No players found");
        }

        List<Player> topPlayersSorted = topPlayers.stream()
            .sorted(Comparator.comparingInt(Player::getNumberOfWins).reversed())
            .limit(playerLimit)
            .collect(Collectors.toList());

        return topPlayersSorted;
    }

    public void processWerewolf (Long lobbyId) {
        List<Player> killedPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsKilled(lobbyId, Boolean.TRUE);

        if (!killedPlayers.isEmpty()) {
            // Randomly select one player to keep as killed
            Player playerToKeepKilled = killedPlayers.get(rand.nextInt(killedPlayers.size()));
            log.info("{} got selected to be killed", playerToKeepKilled.getUsername());
            //permanent eliminated from game
            playerToKeepKilled.setIsAlive(Boolean.FALSE);
            // Set all killed players' isKilled to false, except the randomly selected one
            for (Player player : killedPlayers) {         
                if (player.getIsKilled().equals(Boolean.TRUE) && !player.equals(playerToKeepKilled)) {
                    player.setIsKilled(false);
                }
            }
            repositoryProvider.getPlayerRepository().saveAll(killedPlayers);
        }
    }

    public void processSacrifice (Long lobbyId) {
        List<Player> sacrificedPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsSacrificed(lobbyId, Boolean.TRUE);

        if (!sacrificedPlayers.isEmpty()) {
            for (Player playerToSacrifice : sacrificedPlayers) {
                playerToSacrifice.setIsKilled(Boolean.TRUE);
                playerToSacrifice.setIsAlive(Boolean.FALSE);
                playerToSacrifice.setIsSacrificed(Boolean.FALSE);
                log.info("{} got sacrificed!!", playerToSacrifice.getUsername());
            }
            repositoryProvider.getPlayerRepository().saveAll(sacrificedPlayers);
        }
    }

    public void processProtect (Long lobbyId) {
        List<Player> protectedPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsProtected(lobbyId, Boolean.TRUE);

        if (!protectedPlayers.isEmpty()) {
            for (Player playerToProtect : protectedPlayers) {
                playerToProtect.setIsKilled(Boolean.FALSE);
                playerToProtect.setIsAlive(Boolean.TRUE);
                playerToProtect.setIsProtected(Boolean.FALSE);
                log.info("Player {} is protected", playerToProtect.getUsername());
            }
            repositoryProvider.getPlayerRepository().saveAll(protectedPlayers);
        }
    }
}
