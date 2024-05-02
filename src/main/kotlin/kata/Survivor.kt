package kata

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kata.Status.ALIVE
import kata.Status.DEAD

enum class Status {
    ALIVE, DEAD
}

data class Equipment(val name: String, val location: Equipment.Location) {
    sealed class Location
    data object InHand : Location()
    data object InBackpack : Location()
}

sealed class EquipError

data object MaxEquipmentInHandReached : EquipError()

data class Survivor(
    val name: String,
    val wounds: Int = 0,
    val status: Status = ALIVE,
    val equippedWith: List<Equipment> = emptyList()
) {

    fun applyWound(): Survivor = when {
        wounds.inc() == 2 -> this.copy(wounds = wounds.inc(), status = DEAD)
        wounds.inc() > 2 -> this
        else -> this.copy(wounds = wounds.inc())
    }

    fun equip(equipment: Equipment): Either<EquipError, Survivor> = when {
        equippedWith.count { it.location == Equipment.InHand } >= 2 -> MaxEquipmentInHandReached.left()
        else -> this.copy(equippedWith = equippedWith + equipment).right()
    }
}