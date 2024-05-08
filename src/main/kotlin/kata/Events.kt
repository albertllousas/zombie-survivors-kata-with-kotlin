package kata

import java.time.LocalDateTime

sealed interface GameError

sealed class GameEvent {
    abstract val on: LocalDateTime
}

data class GameStarted(override val on: LocalDateTime) : GameEvent()

data class GameLeveledUp(override val on: LocalDateTime, val level: Level): GameEvent()

data class SurvivorAdded(override val on: LocalDateTime, val survivor: String) : GameEvent()

sealed class SurvivorEvent : GameEvent()

data class EquipmentAdded(override val on: LocalDateTime, val survivor: String, val equipment: String) : SurvivorEvent()

data class EquipmentDiscarded(override val on: LocalDateTime, val survivor: String, val equipment: String) : SurvivorEvent()

data class SurvivorWounded(override val on: LocalDateTime, val survivor: String): SurvivorEvent()

data class SurvivorDied(override val on: LocalDateTime, val survivor: String): SurvivorEvent()

data class SurvivorLeveledUp(override val on: LocalDateTime, val survivor: String, val level: Level): SurvivorEvent()

data class ZombieKilled(override val on: LocalDateTime, val by: String): SurvivorEvent()
