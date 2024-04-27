package kata

import kata.Status.ALIVE
import kata.Status.DEAD

enum class Status {
    ALIVE, DEAD
}

data class Survivor(val name: String, val wounds: Int = 0, val status: Status = ALIVE) {

    fun applyWound(): Survivor = when {
        wounds.inc() == 2 -> this.copy(wounds = wounds.inc(), status = DEAD)
        wounds.inc() > 2 -> this
        else -> this.copy(wounds = wounds.inc())
    }
}