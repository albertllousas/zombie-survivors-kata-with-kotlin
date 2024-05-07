package kata

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kata.Equipment.InHand
import kata.Equipment.InReserve
import kata.Status.ALIVE
import kata.Status.DEAD
import java.time.Clock
import java.time.LocalDateTime

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
    val events: List<Event> = emptyList(),
    val clock: Clock = Clock.systemUTC(),
) {

    fun applyWound(): Survivor = when {
        wounds.inc() == 2 -> this.copy(
            wounds = wounds.inc(),
            status = DEAD,
            events = events + Wounded(LocalDateTime.now(clock), this.name)
        )
        wounds.inc() < 2 -> this.copy(
            wounds = wounds.inc(),
            numOfItemsCanCarry = numOfItemsCanCarry.dec(),
            events = events + Wounded(LocalDateTime.now(clock), this.name)
        ).discardItemIfMaxCapacityReached()
        else -> this
    }

    fun equip(equipment: Equipment): Either<EquipError, Survivor> = when {
        equippedWith.count().inc() > numOfItemsCanCarry -> MaxEquipmentCapacityReached.left()
        equippedWith.count { it.location == InHand } >= 2 -> MaxEquipmentInHandReached.left()
        else -> this.copy(
            equippedWith = equippedWith + equipment,
            events = events + EquipmentAdded(LocalDateTime.now(clock), this.name, equipment.name)
        ).right()
    }

    private fun discardItemIfMaxCapacityReached() = if (numOfItemsCanCarry < this.equippedWith.size) {
        val firstInReserve = equippedWith.firstOrNull { it.location == InReserve }
        val filteredList = firstInReserve?.let { equippedWith.filterNot { it == firstInReserve } } ?: equippedWith
        this.copy(equippedWith = filteredList)
    } else this

    fun killZombie(): Survivor = this.copy(experience = experience.inc()).let { it.levelUp() }

    private fun levelUp() = when {
        experience in 7..18 -> Level.YELLOW
        experience in 19..42 -> Level.ORANGE
        experience > 42 -> Level.RED
        else -> level
    }.let { this.copy(level = it) }
}
