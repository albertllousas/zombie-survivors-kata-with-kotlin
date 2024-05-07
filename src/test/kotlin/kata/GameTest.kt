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
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class GameTest {

    private val fixedClock = Clock.fixed(Instant.parse("2007-12-03T10:15:30.00Z"), ZoneId.of("UTC"))

    @Test
    fun `should begin a game`() {
        Game.start(fixedClock) shouldBe Game(
            survivors = emptyList(),
            status = ONGOING,
            level = BLUE,
            clock = fixedClock,
            history = listOf(GameStarted(on = LocalDateTime.parse("2007-12-03T10:15:30.00"))),
        )
    }

    @Test
    fun `should be able to add survivors to it at any time`() {
        val game = Game.start(fixedClock)
        val survivor = Survivor("Maverick Steel")

        val result = game.add(survivor)

        result.isRight() shouldBe true
        result.onRight {  it.survivors shouldBe listOf(survivor) }
    }

    @Test
    fun `should ensure survivor names within a game are unique`() {
        val game = Game.start()
        val survivor = Survivor("Maverick Steel")

        val result = game.add(survivor).flatMap { it.add(survivor) }

        result shouldBe SurvivorNameAlreadyUsed.left()
    }

    @Test
    fun `should end immediately if all of its survivors have died`() {
        val survivor = Survivor("Maverick Steel", wounds = 1)
        val game = Game(survivors = listOf(survivor), status = ONGOING, clock = fixedClock)

        val result = game.runTurn("Maverick Steel") { it.applyWound().right() }

        result.isRight() shouldBe true
        result.onRight {  it.status shouldBe ENDED }
    }

    @Test
    fun `should check that level equals to the level of the highest living survivor's one when they level-up`() {
        val survivor = Survivor("Max Ryder", experience = 6)
        val game = Game(survivors = listOf(survivor), status = ONGOING, level = BLUE, clock = fixedClock)

        val result = game.runTurn("Max Ryder") { it.killZombie().right() }

        result.isRight() shouldBe true
        result.onRight { it.level shouldBe YELLOW }
    }

    @Test
    fun `should check that level equals to the level of the highest living survivor's one when they join`() {
       val game = Game.start()

        val result = game.add(Survivor("Maverick Steel", level = YELLOW))
            .flatMap { it.add(Survivor("Max Ryder", level = RED)) }

        result.isRight() shouldBe true
        result.onRight { it.level shouldBe RED }
    }
}
