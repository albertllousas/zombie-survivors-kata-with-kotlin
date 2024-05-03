package kata

data class Game(val survivors: List<Survivor>) {
    companion object {
        fun start(): Game = Game(listOf())
    }
}
