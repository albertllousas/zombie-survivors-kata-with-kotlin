package kata

import arrow.core.flatMap
import arrow.core.left
import io.kotest.matchers.shouldBe
import kata.Equipment.InHand
import kata.Equipment.InReserve
import kata.Level.*
import kata.Status.ALIVE
import kata.Status.DEAD
import org.junit.jupiter.api.Test

class SurvivorTest {

    @Test
    fun `should create a survivor`() {
        Survivor(name = "Maverick Steel") shouldBe Survivor(
            name = "Maverick Steel",
            wounds = 0,
            numOfItemsCanCarry = 5,
            status = ALIVE,
            equippedWith = emptyList(),
            experience = 0,
            level = BLUE
        )
    }

    @Test
    fun `should dies immediately when receives two wounds`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5)

        val woundedSurvivor = survivor.applyWound().applyWound()

        woundedSurvivor.status shouldBe DEAD
    }

    @Test
    fun `should not receive additional wounds when survivor is already dead`() {
        val survivor = Survivor(name = "Maverick Steel", wounds = 2, status = DEAD, numOfItemsCanCarry = 5)

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
    fun `should fail trying to equip items in reserve if max capacity is reached`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 2)

        val result = survivor.equip(Equipment("Baseball bat", InReserve))
            .flatMap { it.equip(Equipment("Frying pan", InReserve)) }
            .flatMap { it.equip(Equipment("Bottled Water", InReserve)) }

        result shouldBe MaxEquipmentCapacityReached.left()
    }

    @Test
    fun `each wound reduces the number of pieces of equipment they can carry by one`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5)

        val woundedSurvivor = survivor.applyWound()

        woundedSurvivor.numOfItemsCanCarry shouldBe 4
    }

    @Test
    fun `after a wound, if the number of items can carry is excessive, the first one in reserve will be discarded`() {
        val survivor = Survivor(
            name = "Maverick Steel",
            numOfItemsCanCarry = 2,
            equippedWith = listOf(
                Equipment("Baseball bat", InHand),
                Equipment("Frying pan", InReserve),
                Equipment("Molotov", InReserve)
            )
        )
        val woundedSurvivor = survivor.applyWound()
        woundedSurvivor.equippedWith shouldBe listOf(
            Equipment("Baseball bat", InHand), Equipment("Molotov", InReserve)
        )
    }

    @Test
    fun `should gain one experience each time kills a zombie`() {
        val survivor = Survivor("Max Ryder")

        val result = survivor.killZombie()

        result.experience shouldBe 1
    }
}
