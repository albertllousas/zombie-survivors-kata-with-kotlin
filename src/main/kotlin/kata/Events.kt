package kata

import java.time.LocalDateTime

sealed interface GameError

sealed class Event {
    abstract val on: LocalDateTime
}

data class EquipmentAdded(override val on: LocalDateTime, val survivor: String, val equipment: String) : Event()

data class EquipmentDiscarded(override val on: LocalDateTime, val survivor: String, val equipment: String) : Event()

data class GameStarted(override val on: LocalDateTime) : Event()

data class SurvivorWounded(override val on: LocalDateTime, val survivor: String): Event()

data class SurvivorDied(override val on: LocalDateTime, val survivor: String): Event()

data class SurvivorLeveledUp(override val on: LocalDateTime, val survivor: String, val level: Level): Event()

