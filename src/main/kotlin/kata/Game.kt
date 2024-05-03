package kata

import arrow.core.Either
import arrow.core.left
import arrow.core.right

data object SurvivorNameAlreadyUsed

data class Game(val survivors: List<Survivor>) {

    fun add(survivor: Survivor): Either<SurvivorNameAlreadyUsed, Game> =
        if(survivors.firstOrNull { it.name == survivor.name } != null) SurvivorNameAlreadyUsed.left()
        else this.copy(survivors= survivors + survivor).right()

    companion object {
        fun start(): Game = Game(listOf())
    }
}
