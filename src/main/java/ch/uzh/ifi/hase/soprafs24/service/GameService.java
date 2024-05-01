package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.LobbyCodeGenerator;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Werewolf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private Random rand = new Random();

    private final RepositoryProvider repositoryProvider;
    private final ServiceProvider serviceProvider;
    private final Werewolf werewolf;

    @Autowired
    public GameService(RepositoryProvider repositoryProvider, ServiceProvider serviceProvider, Werewolf werewolf) {
        this.repositoryProvider = repositoryProvider;
        this.serviceProvider = serviceProvider;
        this.werewolf = werewolf;
    }

    public Player createPlayer(Player newPlayer) {
        try {
            newPlayer.setToken(UUID.randomUUID().toString());
            serviceProvider.getPlayerService().checkIfUserExists(newPlayer); // check if the same username is entered
            serviceProvider.getLobbyService().checkIfLobbyExists(newPlayer); // check if the lobby code is correct

            // lobby exists and username is not taken
            // check if the lobby is full
            serviceProvider.getLobbyService().checkIfLobbyFull(newPlayer.getLobbyCode());

            // set the fields for a new player
            newPlayer.setIsAlive(Boolean.TRUE);
            newPlayer.setIsProtected(Boolean.FALSE);
            newPlayer.setIsKilled(Boolean.FALSE);
            newPlayer.setIsReady(Boolean.FALSE);
            newPlayer.setNumberOfVotes(0);

            // get the lobby id by the lobby code
            newPlayer.setLobbyId(repositoryProvider.getLobbyRepository().findByLobbyCode(newPlayer.getLobbyCode()).getLobbyId());

            newPlayer = repositoryProvider.getPlayerRepository().save(newPlayer);
            repositoryProvider.getPlayerRepository().flush();

            log.debug("Created Information for User: {}", newPlayer);
            return newPlayer;
        }
        catch (Exception ex){
            log.info("Player was not created, try again!");
            throw ex;
        }
    }

    public void deletePlayer(Long playerToBeDeletedId){
        repositoryProvider.getPlayerRepository().deleteByPlayerId(playerToBeDeletedId);
    }

    public Lobby createLobby(Lobby newLobby) {
        try{
            String lobbyCode = LobbyCodeGenerator.generateLobbyCode();

            newLobby.setLobbyCode(lobbyCode);
            newLobby.setGameState(GameState.WAITINGROOM);
            newLobby.setCountNightaction(0);
            newLobby.setWinnerSide(WinnerSide.NOWINNER);
            newLobby.setGameSettings(serviceProvider.getLobbyService().setDefaultSettings(newLobby.getNumberOfPlayers()));

            newLobby = repositoryProvider.getLobbyRepository().save(newLobby);
            repositoryProvider.getLobbyRepository().flush();

            // Trigger the Creation of Host Player
            Player hostPlayer = new Player(newLobby.getHostName(), newLobby.getLobbyCode());
            createPlayer(hostPlayer);

            List<Player> players = serviceProvider.getLobbyService().getListOfLobbyPlayers(lobbyCode);
            newLobby.setPlayers(players);

            log.debug("Created Information for Lobby: {}", newLobby);
            return newLobby;
        }
        catch(Exception ex){
            log.info("Player was not created, try again!");
            throw ex;
        }
    }

    @Transactional
    public void goToNextPhase (Long lobbyId) {
        if(!serviceProvider.getPlayerService().areAllPlayersReady(lobbyId)) {
            log.info("Not all Players are ready yet to go to next Phase");
        } else {
            Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);
            switch (lobby.getGameState()) {
                case WAITINGROOM:
                    lobby.setGameState(GameState.NIGHT);
                    break;
                case NIGHT:
                    lobby.setGameState(GameState.REVEALNIGHT);
                    break;
                case REVEALNIGHT:
                    lobby.setGameState(GameState.DISCUSSION);
                    serviceProvider.getPlayerService().resetIsKilled(lobbyId);
                    checkIfgameEnded(lobbyId);
                    break;
                case DISCUSSION:
                    lobby.setGameState(GameState.VOTING);
                    break;
                case VOTING:
                    processVoting(lobbyId);
                    lobby.setGameState(GameState.REVEALVOTING);
                    break;
                case REVEALVOTING:
                    lobby.setGameState(GameState.NIGHT);
                    serviceProvider.getPlayerService().resetVotes(lobbyId);
                    serviceProvider.getPlayerService().resetIsKilled(lobbyId);
                    checkIfgameEnded(lobbyId);
                    break;
                case ENDGAME:
                    resetGame(lobbyId);
                    lobby.setGameState(GameState.WAITINGROOM);
                    break;
                default:
                    break;
            }
            log.info("lobby {} is now in phase {}", lobby.getLobbyId(), lobby.getGameState());

            //reset all alive players to isReady = false
            serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
        }
    }

    public void processNightphase(Long lobbyId) {
        Lobby lobbyToProcess = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);

        if(serviceProvider.getPlayerService().numberOfPlayersAlive(lobbyId) == lobbyToProcess.getCountNightaction()) {
            //process nightaction
            List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

            List<Player> killedPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsKilled(lobbyId, Boolean.TRUE);

            if (!killedPlayers.isEmpty()) {
                    // Randomly select one player to keep as killed
                    Player playerToKeepKilled = killedPlayers.get(rand.nextInt(killedPlayers.size()));
                    //permanent eliminated from game
                    playerToKeepKilled.setIsAlive(false);                
                    // Set all killed players' isKilled to false, except the randomly selected one
                    for (Player player : players) {         
                        if (player.getIsKilled().equals(Boolean.TRUE) && !player.equals(playerToKeepKilled)) {
                            player.setIsKilled(false);
                        }
                    }
            }
            //reset CountNightaction
            serviceProvider.getLobbyService().resetNightactionCount(lobbyId);
        } else {
            log.info("Waiting for Players to perform their Nightaction");
        }

    }

    public void werewolfNightAction(String selection) {
        werewolf.setSelection(selection);
        werewolf.doNightAction();
    }

    private void processVoting (Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        Player playerWithMostVotes = null;
        int maxVotes = -1;
        int numOfMaxVotes = 0;

        for (Player player : players) {
            if (player.getNumberOfVotes() >= maxVotes) {
                //check if other player also has maxVotes
                if(player.getNumberOfVotes() == maxVotes) {
                    numOfMaxVotes++;
                } else {
                    maxVotes = player.getNumberOfVotes();
                    numOfMaxVotes = 1;
                    playerWithMostVotes = player;
                }
            }
        }

        if (playerWithMostVotes != null && maxVotes > 0 && numOfMaxVotes == 1) {
            playerWithMostVotes.setIsKilled(true);
            playerWithMostVotes.setIsAlive(false);
        }
    }

    private void checkIfgameEnded (Long lobbyId) {
        // List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        // //only check alive players
        // List<Player> alivePlayers = players.stream()
        //                                     .filter(Player::getIsAlive)
        //                                     .collect(Collectors.toList());

        List<Player> alivePlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsAlive(lobbyId, Boolean.TRUE);

        int countWerewolf = 0;
        int countVillager = 0;

        for (Player player : alivePlayers) {
            if (player.getRoleName().equals("Werewolf")) {
                countWerewolf++;
            } else {
                countVillager++;
            }
        }

        Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);

        if (countWerewolf == 0) {
            lobby.setWinnerSide(WinnerSide.VILLAGERS);
            lobby.setGameState(GameState.ENDGAME);
        } else if (countWerewolf >= countVillager) {
            lobby.setWinnerSide(WinnerSide.WEREWOLVES);
            lobby.setGameState(GameState.ENDGAME);
        }
    }

    private void resetGame (Long lobbyId) {
        serviceProvider.getLobbyService().resetLobby(lobbyId);
        serviceProvider.getPlayerService().resetPlayersByLobbyId(lobbyId);
    }
}
