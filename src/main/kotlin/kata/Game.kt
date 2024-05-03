package kata

data class Game(val survivors: List<Survivor>) {

    fun add(survivor: Survivor) = this.copy(survivors= survivors + survivor)

    companion object {
        fun start(): Game = Game(listOf())
    }
}
