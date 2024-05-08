package kata

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kata.GameStatus.ENDED
import kata.GameStatus.ONGOING
import kata.Level.BLUE
import java.time.Clock
import java.time.LocalDateTime.now
import java.time.LocalDateTime.parse

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
    val history: List<GameEvent> = emptyList(),
) {

    fun add(survivor: Survivor): Either<SurvivorNameAlreadyUsed, Game> =
        if (survivors.firstOrNull { it.name == survivor.name } != null) SurvivorNameAlreadyUsed.left()
        else this.copy(survivors = survivors + survivor, history = history + SurvivorAdded(now(clock), survivor.name))
            .let { it.adjustLevel() }.right()

    fun runTurn(survivorName: String, actionOn: (Survivor) -> Either<GameError, Survivor>): Either<GameError, Game> =
        survivors.find { it.name == survivorName }
            ?.let { old ->
                actionOn(old)
                    .map { it.getAndClearEvents() }
                    .map { (events, updated) ->
                        this.copy(
                            survivors = survivors.map { if (old.name == it.name) updated else old },
                            history = history + events,
                        )
                    }
            }
            ?.map { it.adjustStatus() }
            ?.map { it.adjustLevel() }
            ?: this.right()

    private fun adjustLevel(): Game {
        val newLevel = this.survivors.maxByOrNull { it.level.ordinal }?.level ?: BLUE
        val newEvents = if(newLevel != level) listOf(GameLeveledUp(now(clock), newLevel)) else emptyList()
        return this.copy(level = newLevel, history = history + newEvents)
    }

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
