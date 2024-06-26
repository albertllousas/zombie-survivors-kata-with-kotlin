package kata

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kata.Equipment.InHand
import kata.Equipment.InReserve
import kata.Status.ALIVE
import kata.Status.DEAD
import java.time.Clock
import java.time.LocalDateTime.now

enum class Status {
    ALIVE, DEAD
}

data class Equipment(val name: String, val location: Location) {
    sealed class Location
    data object InHand : Location()
    data object InReserve : Location()
}

sealed class EquipError : GameError
data object MaxEquipmentInHandReached : EquipError()
data object MaxEquipmentCapacityReached : EquipError()

data class Survivor(
    val name: String,
    val wounds: Int = 0,
    val status: Status = ALIVE,
    val equippedWith: List<Equipment> = emptyList(),
    val numOfItemsCanCarry: Int = 5,
    val experience: Int = 0,
    val level: Level = Level.BLUE,
    val events: List<GameEvent> = emptyList(),
    val clock: Clock = Clock.systemUTC(),
) {

    fun applyWound(): Survivor = when {
        wounds.inc() == 2 -> this.copy(
            wounds = wounds.inc(),
            status = DEAD,
            events = events + SurvivorWounded(now(clock), this.name) + SurvivorDied(now(clock), this.name)
        )

        wounds.inc() < 2 -> this.copy(
            wounds = wounds.inc(),
            numOfItemsCanCarry = numOfItemsCanCarry.dec(),
            events = events + SurvivorWounded(now(clock), this.name)
        ).discardItemIfMaxCapacityReached()

        else -> this
    }

    fun equip(equipment: Equipment): Either<EquipError, Survivor> = when {
        equippedWith.count().inc() > numOfItemsCanCarry -> MaxEquipmentCapacityReached.left()
        equippedWith.count { it.location == InHand } >= 2 -> MaxEquipmentInHandReached.left()
        else -> this.copy(
            equippedWith = equippedWith + equipment,
            events = events + EquipmentAdded(now(clock), this.name, equipment.name)
        ).right()
    }

    private fun discardItemIfMaxCapacityReached() =
        if (numOfItemsCanCarry < this.equippedWith.size) {
            val firstInReserve = equippedWith.firstOrNull { it.location == InReserve }
            val filteredList = firstInReserve?.let { equippedWith.filterNot { it == firstInReserve } } ?: equippedWith
            this.copy(
                equippedWith = filteredList,
                events = firstInReserve
                    ?.let { events + EquipmentDiscarded(now(clock), this.name, firstInReserve.name) }
                    ?: events
            )
        } else this

    fun killZombie(): Survivor = this.copy(
        experience = experience.inc(),
        events = events + ZombieKilled(on = now(clock), by = this.name)
    ).let { it.levelUp() }

    private fun levelUp() = when {
        experience in 7..18 -> Level.YELLOW
        experience in 19..42 -> Level.ORANGE
        experience > 42 -> Level.RED
        else -> level
    }.let {
        val newEvents = if(it != level) listOf(SurvivorLeveledUp(now(clock), this.name, it)) else emptyList()
        this.copy(level = it, events = events + newEvents)
    }

    fun getAndClearEvents(): Pair<List<GameEvent>, Survivor> = Pair(events, this.copy(events = emptyList()))
}
