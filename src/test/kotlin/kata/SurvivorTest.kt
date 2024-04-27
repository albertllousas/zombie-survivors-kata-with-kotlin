package kata

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SurvivorTest {

    @Test
    fun `should create a survivor`() {
        Survivor(name = "Maverick Steel") shouldBe Survivor(name = "Maverick Steel", wounds = 0)
    }
}
