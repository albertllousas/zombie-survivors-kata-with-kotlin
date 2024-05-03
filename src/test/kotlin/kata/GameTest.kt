package kata

import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GameTest {

    @Test
    fun `should begin with zero survivors`() {
        Game.start() shouldBe Game(survivors = listOf())
    }

    @Test
    fun `should be able to add survivors to it at any time`() {
        val game = Game.start()
        val survivor = Survivor("Maverick Steel")

        val gameWithSurvivors = game.add(survivor)

        gameWithSurvivors shouldBe Game(survivors = listOf(survivor)).right()
    }

    @Test
    fun `should ensure survivor names within a game are unique`() {
        val game = Game.start()
        val survivor = Survivor("Maverick Steel")

        val gameWithSurvivors = game.add(survivor).flatMap { it.add(survivor) }

        gameWithSurvivors shouldBe SurvivorNameAlreadyUsed.left()
    }
}
