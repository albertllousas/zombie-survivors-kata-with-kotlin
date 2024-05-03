package kata

import arrow.core.flatMap
import arrow.core.left
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

        result.isRight() shouldBe true
        result.onRight { it.equippedWith shouldBe listOf(Equipment("Baseball bat", InHand)) }
    }

    @Test
    fun `should fail trying to equip more than two items in hand`() {
        val survivor = Survivor(name = "Maverick Steel")

        val result = survivor.equip(Equipment("Baseball bat", InHand))
            .flatMap { it.equip(Equipment("Pistol", InHand)) }
            .flatMap { it.equip(Equipment("Knife", InHand)) }

        result shouldBe MaxEquipmentInHandReached.left()
    }

    @Test
    fun `should fail trying to equip more than 3 items in reserve`() {
        val survivor = Survivor(name = "Maverick Steel")

        val result = survivor.equip(Equipment("Baseball bat", InReserve))
            .flatMap { it.equip(Equipment("Frying pan", InReserve)) }
            .flatMap { it.equip(Equipment("Bottled Water", InReserve)) }
            .flatMap { it.equip(Equipment("Molotov", InReserve)) }

        result shouldBe MaxEquipmentInReserveReached.left()
    }
}
