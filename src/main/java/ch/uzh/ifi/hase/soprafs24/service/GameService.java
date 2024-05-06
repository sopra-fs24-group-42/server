package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.LobbyCodeGenerator;
import ch.uzh.ifi.hase.soprafs24.utils.Role;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Protector;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Sacrifice;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Werewolf;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.SelectionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private Random rand = new Random();

    private final RepositoryProvider repositoryProvider;
    private final ServiceProvider serviceProvider;
    private final Werewolf werewolf;
    private final Sacrifice sacrifice;
    private final Protector protector;

    @Autowired
    public GameService(RepositoryProvider repositoryProvider, ServiceProvider serviceProvider, Werewolf werewolf, Sacrifice sacrifice, Protector protector) {
        this.repositoryProvider = repositoryProvider;
        this.serviceProvider = serviceProvider;
        this.werewolf = werewolf;
        this.sacrifice = sacrifice;
        this.protector = protector;
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
            newPlayer.setIsSacrificed(Boolean.FALSE);
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
            boolean isNarrationActive = false; //add field in lobby in case we want to be able to deactivate it
            switch (lobby.getGameState()) {
                case WAITINGROOM:
                    if(isNarrationActive) {
                        lobby.setGameState(GameState.PRENIGHT);
                        hostNotReady(lobbyId);
                    } else {
                        lobby.setGameState(GameState.NIGHT);
                        serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    }
                    break;
                case PRENIGHT:
                    lobby.setGameState(GameState.NIGHT);
                    serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    break;
                case NIGHT:
                    lobby.setGameState(GameState.REVEALNIGHT);
                    serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    break;
                case REVEALNIGHT:
                    lobby.setGameState(GameState.DISCUSSION);
                    serviceProvider.getPlayerService().resetIsKilled(lobbyId);
                    checkIfgameEnded(lobbyId);
                    serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    break;
                case DISCUSSION:
                    if(isNarrationActive) {
                        lobby.setGameState(GameState.PREVOTING);
                        hostNotReady(lobbyId);
                    } else {
                        lobby.setGameState(GameState.VOTING);
                        serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    }
                    break;
                case PREVOTING:
                    lobby.setGameState(GameState.VOTING);
                    serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    break;
                case VOTING:
                    processVoting(lobbyId);
                    lobby.setGameState(GameState.REVEALVOTING);
                    serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    break;
                case REVEALVOTING:
                    if(isNarrationActive) {
                        lobby.setGameState(GameState.PRENIGHT);
                        hostNotReady(lobbyId);
                    } else {
                        lobby.setGameState(GameState.NIGHT);
                        serviceProvider.getPlayerService().resetVotes(lobbyId);
                        serviceProvider.getPlayerService().resetIsKilled(lobbyId);
                        checkIfgameEnded(lobbyId);
                        serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    }
                    break;
                case ENDGAME:
                    resetGame(lobbyId);
                    lobby.setGameState(GameState.WAITINGROOM);
                    serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
                    break;
                default:
                    break;
            }
            repositoryProvider.getLobbyRepository().save(lobby);
            log.info("lobby {} is now in phase {}", lobby.getLobbyId(), lobby.getGameState());
        }
    }

    public void processNightphase(Long lobbyId) {
        Lobby lobbyToProcess = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);

        //only process if everyone alive did nightaction
        if(serviceProvider.getPlayerService().numberOfPlayersAlive(lobbyId) == lobbyToProcess.getCountNightaction()) {
            log.info("start processNight");
            
            //List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

            List<Player> killedPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsKilled(lobbyId, Boolean.TRUE);

            if (!killedPlayers.isEmpty()) {
                log.info("there are players killed");
                // Randomly select one player to keep as killed
                Player playerToKeepKilled = killedPlayers.get(rand.nextInt(killedPlayers.size()));
                //permanent eliminated from game
                playerToKeepKilled.setIsAlive(false);
                // Set all killed players' isKilled to false, except the randomly selected one
                for (Player player : killedPlayers) {         
                    if (player.getIsKilled().equals(Boolean.TRUE) && !player.equals(playerToKeepKilled)) {
                        player.setIsKilled(false);
                    }
                }
            }

            repositoryProvider.getPlayerRepository().saveAll(killedPlayers);

            //kill sacrificed Players from game
            List<Player> sacrificedPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsSacrificed(lobbyId, Boolean.TRUE);

            if (!sacrificedPlayers.isEmpty()) {
                log.info("players get sacrificed");
                for (Player playerToSacrifice : sacrificedPlayers) {
                    playerToSacrifice.setIsKilled(Boolean.TRUE);
                    playerToSacrifice.setIsAlive(Boolean.FALSE);
                    playerToSacrifice.setIsSacrificed(Boolean.FALSE);
                    log.info("{} got sacrificed!!", playerToSacrifice.getUsername());
                }
            }

            repositoryProvider.getPlayerRepository().saveAll(sacrificedPlayers);

            //let protected Players survive
            List<Player> protectedPlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsProtected(lobbyId, Boolean.TRUE);

            if (!protectedPlayers.isEmpty()) {
                for (Player playerToProtect : protectedPlayers) {
                    playerToProtect.setIsKilled(Boolean.FALSE);
                    playerToProtect.setIsAlive(Boolean.TRUE);
                    playerToProtect.setIsProtected(Boolean.FALSE);
                    log.info("Player {} is protected", playerToProtect.getUsername());
                }
            }

            repositoryProvider.getPlayerRepository().saveAll(protectedPlayers);

            //reset CountNightaction
            serviceProvider.getLobbyService().resetNightactionCount(lobbyId);
        } else {
            log.info("Waiting for Players to perform their Nightaction");
        }

    }

    public void werewolfNightAction(SelectionRequest request) {
        werewolf.setSelection(request.getSelection());
        werewolf.setUsername(request.getUsername());

        // check if it is a correct role
        if(isValidForNightAction(werewolf) && checkRole(werewolf.getUsername(), werewolf.getRoleName())){
            werewolf.doNightAction();
        }
    }

    private boolean isValidForNightAction(Role role){
        if(role.getSelection().isEmpty()){
            log.info("Player {} does not select anyone", role.getUsername());
            return false;
        }

        if(!serviceProvider.getPlayerService().playersLobbyEqual(role.getUsername(), role.getSelection())) {
            log.info("PLayers {} and {} are not in the same lobby", role.getUsername(), role.getSelection());
            return false;
        }
        return true;
    }

    public void protectorNightAction(SelectionRequest request) {
        protector.setSelection(request.getSelection());
        protector.setUsername(request.getUsername());

        if(isValidForNightAction(protector) && checkRole(request.getUsername(), protector.getRoleName())){
            protector.doNightAction();
        }
    }

    public void sacrificeNightAction(SelectionRequest request) {
        sacrifice.setSelection(request.getSelection());
        sacrifice.setUsername(request.getUsername());

        if(isValidForNightAction(sacrifice) && checkRole(request.getUsername(), sacrifice.getRoleName())){
            sacrifice.doNightAction();
        }
    }

    private boolean checkRole(String userName, String roleName){
        Player player = repositoryProvider.getPlayerRepository().findByUsername(userName);
        if (player.getRoleName().equals(roleName)){
            return true;
        }
        log.info("Player {} has role {}, but requested {}", userName, player.getRoleName(), roleName);
        return false;

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

    private void hostNotReady(Long lobbyId) {
        Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);
        Player hostPlayer = repositoryProvider.getPlayerRepository().findByUsername(lobby.getHostName());

        hostPlayer.setIsReady(Boolean.FALSE);
        repositoryProvider.getPlayerRepository().save(hostPlayer);
    }
}
