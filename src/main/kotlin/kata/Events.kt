package kata

import java.time.LocalDateTime

sealed interface GameError

sealed class Event {
    abstract val on: LocalDateTime
}

data class GameStarted(override val on: LocalDateTime) : Event()
