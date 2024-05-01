package kata

import io.kotest.matchers.shouldBe
import kata.Equipment.*
import kata.Status.DEAD
import org.junit.jupiter.api.Test

class SurvivorTest {

    @Test
    fun `should create a survivor`() {
        Survivor(name = "Maverick Steel") shouldBe Survivor(name = "Maverick Steel", wounds = 0)
    }

    @Test
    fun `should dies immediately when receives two wounds`() {
        val survivor = Survivor(name = "Maverick Steel")

        val woundedSurvivor = survivor.applyWound().applyWound()

        woundedSurvivor.status shouldBe DEAD
    }

    @Test
    fun `should not receive additional wounds when survivor is already dead`() {
        val survivor = Survivor(name = "Maverick Steel", wounds = 2, status = DEAD)

        val woundedSurvivor = survivor.applyWound()

        woundedSurvivor.wounds shouldBe 2
    }

    @Test
    fun `should be able to carry up equipment`() {
        val survivor = Survivor(name = "Maverick Steel")

        val result = survivor.equip(Equipment("Baseball bat", InHand))

        result.equippedWith shouldBe listOf(Equipment("Baseball bat", InHand))
    }
}
