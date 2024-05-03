package kata

import arrow.core.flatMap
import arrow.core.left
import io.kotest.matchers.shouldBe
import kata.Equipment.InHand
import kata.Equipment.InReserve
import kata.Status.DEAD
import org.junit.jupiter.api.Test

class SurvivorTest {

    @Test
    fun `should create a survivor`() {
        Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5) shouldBe Survivor(
            name = "Maverick Steel",
            wounds = 0,
            numOfItemsCanCarry = 5
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
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5)

        val result = survivor.equip(Equipment("Baseball bat", InHand))

        result.isRight() shouldBe true
        result.onRight { it.equippedWith shouldBe listOf(Equipment("Baseball bat", InHand)) }
    }

    @Test
    fun `should fail trying to equip more than two items in hand`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5)

        val result = survivor.equip(Equipment("Baseball bat", InHand))
            .flatMap { it.equip(Equipment("Pistol", InHand)) }
            .flatMap { it.equip(Equipment("Knife", InHand)) }

        result shouldBe MaxEquipmentInHandReached.left()
    }

    @Test
    fun `should fail trying to equip more than 3 items in reserve`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5)

        val result = survivor.equip(Equipment("Baseball bat", InReserve))
            .flatMap { it.equip(Equipment("Frying pan", InReserve)) }
            .flatMap { it.equip(Equipment("Bottled Water", InReserve)) }
            .flatMap { it.equip(Equipment("Molotov", InReserve)) }

        result shouldBe MaxEquipmentInReserveReached.left()
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
}
