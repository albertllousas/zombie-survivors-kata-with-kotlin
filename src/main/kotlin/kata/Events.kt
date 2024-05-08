package kata

import java.time.LocalDateTime

sealed interface GameError

sealed class Event {
    abstract val on: LocalDateTime
}

data class GameStarted(override val on: LocalDateTime) : Event()

data class SurvivorAdded(override val on: LocalDateTime, val survivor: String) : Event()

sealed class SurvivorEvent : Event()

data class EquipmentAdded(override val on: LocalDateTime, val survivor: String, val equipment: String) : SurvivorEvent()

data class EquipmentDiscarded(override val on: LocalDateTime, val survivor: String, val equipment: String) : SurvivorEvent()

data class SurvivorWounded(override val on: LocalDateTime, val survivor: String): SurvivorEvent()

data class SurvivorDied(override val on: LocalDateTime, val survivor: String): SurvivorEvent()

data class SurvivorLeveledUp(override val on: LocalDateTime, val survivor: String, val level: Level): SurvivorEvent()

data class ZombieKilled(override val on: LocalDateTime, val by: String): SurvivorEvent()
