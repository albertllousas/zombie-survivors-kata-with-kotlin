package kata

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kata.GameStatus.ENDED
import kata.GameStatus.ONGOING
import kata.Level.BLUE
import java.time.Clock
import java.time.LocalDateTime.now

enum class GameStatus {
    ONGOING, ENDED
}

enum class Level {
    BLUE, YELLOW, ORANGE, RED
}

data object SurvivorNameAlreadyUsed

data class Game(
    val survivors: List<Survivor>,
    val status: GameStatus,
    val level: Level = BLUE,
    val clock: Clock = Clock.systemUTC(),
    val history: List<Event> = emptyList(),
) {

    fun add(survivor: Survivor): Either<SurvivorNameAlreadyUsed, Game> =
        if (survivors.firstOrNull { it.name == survivor.name } != null) SurvivorNameAlreadyUsed.left()
        else this.copy(survivors = survivors + survivor).let { it.adjustLevel() }.right()

    fun runTurn(survivorName: String, actionOn: (Survivor) -> Either<GameError, Survivor>): Either<GameError, Game> =
        survivors.find { it.name == survivorName }
            ?.let { old ->
                actionOn(old)
                    .map { updated -> this.copy(survivors = survivors.map { if (old.name == it.name) updated else old }) }
            }
            ?.map { it.adjustStatus() }
            ?.map { it.adjustLevel() }
            ?: this.right()

    private fun adjustLevel() = this.copy(level = this.survivors.maxByOrNull { it.level.ordinal }?.level ?: BLUE)

    private fun adjustStatus() =
        if (this.survivors.all { it.status == Status.DEAD }) this.copy(status = ENDED) else this

    companion object {
        fun start(clock: Clock = Clock.systemUTC()): Game = Game(
            survivors = listOf(),
            status = ONGOING,
            clock = clock,
            history = listOf(GameStarted(now(clock)))
        )
    }
}
