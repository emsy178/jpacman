package nl.tudelft.jpacman.jpacman.game;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.level.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the extra lives rule: dying costs a life and respawns the player,
 * and the game ends only after the last life is lost. Uses a ghost-free map
 * so ghosts can't kill the player mid-test.
 */
class SinglePlayerGameTest {

    private static final int STARTING_LIVES = 3;

    private Game game;
    private Player player;

    @BeforeEach
    void setUp() {
        game = new Launcher().withMapFile("/extralivesmap.txt").makeGame();
        player = game.getPlayers().get(0);
        game.start();
    }

    @AfterEach
    void tearDown() {
        game.stop();
    }

    private void die() {
        player.setAlive(false);
        game.levelLost();
    }

    @Test
    void playerStartsAliveWithThreeLives() {
        assertThat(player.getLives()).isEqualTo(STARTING_LIVES);
        assertThat(player.isAlive()).isTrue();
        assertThat(game.isInProgress()).isTrue();
    }

    @Test
    void dyingWithLivesLeftCostsALifeAndKeepsTheGameGoing() {
        die();

        assertThat(player.getLives()).isEqualTo(STARTING_LIVES - 1);
        assertThat(player.isAlive()).isTrue();
        assertThat(game.isInProgress()).isTrue();
    }

    @Test
    void respawnReturnsThePlayerToTheStartingSquare() {
        Square start = player.getSquare();
        game.move(player, Direction.EAST);
        assertThat(player.getSquare()).isNotEqualTo(start);

        die();

        assertThat(player.getSquare()).isEqualTo(start);
    }

    @Test
    void gameEndsOnlyAfterTheLastLifeIsSpent() {
        die();
        die();
        assertThat(game.isInProgress()).isTrue();

        die();

        assertThat(player.getLives()).isZero();
        assertThat(player.isAlive()).isFalse();
        assertThat(game.isInProgress()).isFalse();
    }

    @Test
    void livesNeverGoNegative() {
        die();
        die();
        die();
        die();

        assertThat(player.getLives()).isZero();
    }
}
