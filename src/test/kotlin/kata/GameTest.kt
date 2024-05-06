package kata

import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import kata.GameStatus.ENDED
import kata.GameStatus.ONGOING
import kata.Level.*
import kata.Status.DEAD
import org.junit.jupiter.api.Test

class GameTest {

    @Test
    fun `should begin with default values`() {
        Game.start() shouldBe Game(survivors = listOf(), status = ONGOING, level = BLUE)
    }

    @Test
    fun `should be able to add survivors to it at any time`() {
        val game = Game.start()
        val survivor = Survivor("Maverick Steel")

        val gameWithSurvivors = game.add(survivor)

        gameWithSurvivors shouldBe Game(survivors = listOf(survivor), ONGOING).right()
    }

    @Test
    fun `should ensure survivor names within a game are unique`() {
        val game = Game.start()
        val survivor = Survivor("Maverick Steel")

        val gameWithSurvivors = game.add(survivor).flatMap { it.add(survivor) }

        gameWithSurvivors shouldBe SurvivorNameAlreadyUsed.left()
    }

    @Test
    fun `should end immediately if all of its survivors have died`() {
        val survivor = Survivor("Maverick Steel", wounds = 1)
        val game = Game(survivors = listOf(survivor), status = ONGOING)

        val result = game.runTurn("Maverick Steel") { it.applyWound().right() }

        result shouldBe Game(survivors = listOf(survivor.copy(wounds = 2, status = DEAD)), status = ENDED).right()
    }

    @Test
    fun `should keep the game level always equal to the level of the highest living survivor's level`() {
        val survivor = Survivor("Max Ryder", experience = 6)
        val game = Game(survivors = listOf(survivor), status = ONGOING, level = BLUE)

        val result = game.runTurn("Max Ryder") { it.killZombie().right() }

        result.isRight() shouldBe true
        result.onRight { it.level shouldBe YELLOW }
    }
}
