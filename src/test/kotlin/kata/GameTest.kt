package kata

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GameTest {

    @Test
    fun `should begin with zero survivors`() {
        Game.start() shouldBe Game(survivors = listOf<Survivor>())
    }
}
