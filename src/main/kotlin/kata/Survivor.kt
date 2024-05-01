package kata

import kata.Status.ALIVE
import kata.Status.DEAD

enum class Status {
    ALIVE, DEAD
}

data class Equipment(val name: String, val location: Location) {
    sealed class Location
    data object InHand : Location()
    data object InBackpack : Location()
}

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

    fun equip(equipment: Equipment) = this.copy( equippedWith = equippedWith + equipment)
}