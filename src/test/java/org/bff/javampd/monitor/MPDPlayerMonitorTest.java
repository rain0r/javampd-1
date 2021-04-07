package org.bff.javampd.monitor;

import org.bff.javampd.player.PlayerBasicChangeEvent;
import org.bff.javampd.player.PlayerBasicChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MPDPlayerMonitorTest {

    private PlayerMonitor playerMonitor;

    @BeforeEach
    void setUp() {
        playerMonitor = new MPDPlayerMonitor();
    }

    @Test
    void testAddPlayerChangeListener() {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_STARTED, changeEvent[0].getStatus());
    }

    @Test
    void testRemovePlayerChangeListener() {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        PlayerBasicChangeListener playerBasicChangeListener = event -> changeEvent[0] = event;

        playerMonitor.addPlayerChangeListener(playerBasicChangeListener);
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_STARTED, changeEvent[0].getStatus());

        changeEvent[0] = null;
        playerMonitor.removePlayerChangeListener(playerBasicChangeListener);
        playerMonitor.processResponseStatus("state: stop");
        playerMonitor.checkStatus();
        assertNull(changeEvent[0]);
    }

    @Test
    void testPlayerStarted() {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);
        playerMonitor.processResponseStatus("state: stop");
        playerMonitor.checkStatus();
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_STARTED, changeEvent[0].getStatus());
    }

    @Test
    void testPlayerStopped() {
        processStoppedTest("state: play");
    }

    @Test
    void testPlayerInvalidStatus() {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);
        playerMonitor.processResponseStatus("state: bogus");
        playerMonitor.checkStatus();
        assertNull(changeEvent[0]);
    }

    @Test
    void testPlayerInvalidStatusAfterValidStatus() {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();
        assertNotNull(changeEvent[0]);

        changeEvent[0] = null;
        playerMonitor.processResponseStatus("state: bogus");
        playerMonitor.checkStatus();
        assertNull(changeEvent[0]);
    }

    @Test
    void testPlayerPaused() {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();
        playerMonitor.processResponseStatus("state: pause");
        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_PAUSED, changeEvent[0].getStatus());
    }

    @Test
    void testPlayerPausedtoStopped() {
        processStoppedTest("state: pause");
    }

    private void processStoppedTest(String from) {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);
        playerMonitor.processResponseStatus(from);
        playerMonitor.checkStatus();
        playerMonitor.processResponseStatus("state: stop");
        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_STOPPED, changeEvent[0].getStatus());
    }

    @Test
    void testPlayerUnPaused() {
        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();
        playerMonitor.processResponseStatus("state: pause");
        playerMonitor.checkStatus();
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();

        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_UNPAUSED, changeEvent[0].getStatus());
    }

    @Test
    void testGetStatus() {
        playerMonitor.processResponseStatus("state: play");
        playerMonitor.checkStatus();
        assertEquals(PlayerStatus.STATUS_PLAYING, playerMonitor.getStatus());
    }

    @Test
    void testResetState() {
        String line = "state: play";

        final PlayerBasicChangeEvent[] changeEvent = new PlayerBasicChangeEvent[1];

        playerMonitor.addPlayerChangeListener(event -> changeEvent[0] = event);

        playerMonitor.processResponseStatus(line);
        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_STARTED, changeEvent[0].getStatus());

        playerMonitor.reset();
        changeEvent[0] = null;

        playerMonitor.processResponseStatus(line);
        playerMonitor.checkStatus();
        assertEquals(PlayerBasicChangeEvent.Status.PLAYER_STARTED, changeEvent[0].getStatus());
    }
}
