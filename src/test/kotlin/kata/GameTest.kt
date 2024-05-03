package kata

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GameTest {

    @Test
    fun `should begin with zero survivors`() {
        Game.start() shouldBe Game(survivors = listOf<Survivor>())
    }

    @Test
    fun `should be able to add survivors to it at any time`() {
        val game = Game.start()
        val survivor = Survivor("Maverick Steel")

        val gameWithSurvivors = game.add(survivor)

        gameWithSurvivors shouldBe Game(survivors = listOf(survivor))
    }
}
