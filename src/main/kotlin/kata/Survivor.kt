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
    data object InReserve : Location()
}

sealed class EquipError
data object MaxEquipmentInHandReached : EquipError()
data object MaxEquipmentInReserveReached : EquipError()

data class Survivor(
    val name: String,
    val wounds: Int = 0,
    val status: Status = ALIVE,
    val equippedWith: List<Equipment> = emptyList(),
    val numOfItemsCanCarry: Int = 5
) {

    fun applyWound(): Survivor = when {
        wounds.inc() == 2 -> this.copy(wounds = wounds.inc(), status = DEAD)
        wounds.inc() > 2 -> this
        else -> this.copy(wounds = wounds.inc(), numOfItemsCanCarry = numOfItemsCanCarry.dec())
    }
    fun equip(equipment: Equipment): Either<EquipError, Survivor> = when {
        equippedWith.count { it.location == Equipment.InHand } >= 2 -> MaxEquipmentInHandReached.left()
        equippedWith.count { it.location == Equipment.InReserve } >= 3 -> MaxEquipmentInReserveReached.left()
        else -> this.copy(equippedWith = equippedWith + equipment).right()
    }
}