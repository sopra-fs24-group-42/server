package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import  ch.uzh.ifi.hase.soprafs24.constant.GameState;
import  ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class LobbyServiceTest {

    @Mock
    private RepositoryProvider repositoryProvider;

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    private Lobby testLobby;

    private GameSettings gameSettings;

    @BeforeEach
    public void setupLobby() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(repositoryProvider.getLobbyRepository()).thenReturn(lobbyRepository);

        this.testLobby = new Lobby();
        testLobby.setLobbyId(2L);
        testLobby.setHostName("testHost");
        testLobby.setNumberOfPlayers(7);
        testLobby.setGameState(GameState.NIGHT);
        //testLobby.setCountNightaction(0);
        testLobby.setWinnerSide(WinnerSide.NOWINNER);
        this.gameSettings = Mockito.mock(GameSettings.class);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);

    }

    // @Test
    // void resetNightactionCount_success() {
    //     when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);

    //     lobbyService.resetNightactionCount(testLobby.getLobbyId());
    //     verify(lobbyRepository).findByLobbyId(testLobby.getLobbyId());
    //     assertEquals(0, testLobby.getCountNightaction(), "Night action count should be reset to 0");
    // }

    // @Test
    // void incrementCountNightaction_success() {
    //     when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);

    //     lobbyService.incrementCountNightaction(testLobby.getLobbyId());

    //     verify(lobbyRepository).findByLobbyId(testLobby.getLobbyId());
    //     assertEquals(1, testLobby.getCountNightaction(), "Night action count should be incremented by 1");
    // }

}
