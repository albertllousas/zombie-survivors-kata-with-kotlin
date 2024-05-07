package kata

import arrow.core.flatMap
import arrow.core.left
import io.kotest.matchers.shouldBe
import kata.Equipment.InHand
import kata.Equipment.InReserve
import kata.Level.BLUE
import kata.Level.ORANGE
import kata.Level.RED
import kata.Level.YELLOW
import kata.Status.ALIVE
import kata.Status.DEAD
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class SurvivorTest {

    private val fixedClock = Clock.fixed(Instant.parse("2007-12-03T10:15:30.00Z"), ZoneId.of("UTC"))

    @Test
    fun `should create a survivor`() {
        Survivor(name = "Maverick Steel", clock = fixedClock) shouldBe Survivor(
            name = "Maverick Steel",
            wounds = 0,
            status = ALIVE,
            equippedWith = emptyList(),
            numOfItemsCanCarry = 5,
            experience = 0,
            level = BLUE,
            clock = fixedClock
        )
    }

    @Test
    fun `should dies immediately when receives two wounds`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5, clock = fixedClock)

        val woundedSurvivor = survivor.applyWound().applyWound()

        woundedSurvivor.status shouldBe DEAD
        woundedSurvivor.events shouldBe listOf(
            Wounded(on = LocalDateTime.parse("2007-12-03T10:15:30.00"), survivor = "Maverick Steel"),
            Wounded(on = LocalDateTime.parse("2007-12-03T10:15:30.00"), survivor = "Maverick Steel")
        )
    }

    @Test
    fun `should not receive additional wounds when survivor is already dead`() {
        val survivor = Survivor(
            name = "Maverick Steel",
            wounds = 2,
            status = DEAD,
            clock = fixedClock
        )

        val woundedSurvivor = survivor.applyWound()

        woundedSurvivor.wounds shouldBe 2
    }

    @Test
    fun `should be able to carry up equipment`() {
        val survivor = Survivor(name = "Maverick Steel", clock = fixedClock)

        val result = survivor.equip(Equipment("Baseball bat", InHand))

        result.isRight() shouldBe true
        result.onRight {
            it.equippedWith shouldBe listOf(Equipment("Baseball bat", InHand))
            it.events shouldBe listOf(
                EquipmentAdded(
                    on = LocalDateTime.parse("2007-12-03T10:15:30.00"),
                    survivor = "Maverick Steel",
                    equipment = "Baseball bat"
                )
            )
        }
    }

    @Test
    fun `should fail trying to equip more than two items in hand`() {
        val survivor = Survivor(name = "Maverick Steel", clock = fixedClock)

        val result = survivor.equip(Equipment("Baseball bat", InHand))
            .flatMap { it.equip(Equipment("Pistol", InHand)) }
            .flatMap { it.equip(Equipment("Knife", InHand)) }

        result shouldBe MaxEquipmentInHandReached.left()
    }

    @Test
    fun `should fail trying to equip items in reserve if max capacity is reached`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 2, clock = fixedClock)

        val result = survivor.equip(Equipment("Baseball bat", InReserve))
            .flatMap { it.equip(Equipment("Frying pan", InReserve)) }
            .flatMap { it.equip(Equipment("Bottled Water", InReserve)) }

        result shouldBe MaxEquipmentCapacityReached.left()
    }

    @Test
    fun `each wound reduces the number of pieces of equipment they can carry by one`() {
        val survivor = Survivor(name = "Maverick Steel", numOfItemsCanCarry = 5, clock = fixedClock)

        val woundedSurvivor = survivor.applyWound()

        woundedSurvivor.numOfItemsCanCarry shouldBe 4
    }

    @Test
    fun `after a wound, if the number of items can carry is excessive, the first one in reserve will be discarded`() {
        val survivor = Survivor(
            name = "Maverick Steel",
            equippedWith = listOf(
                Equipment("Baseball bat", InHand),
                Equipment("Frying pan", InReserve),
                Equipment("Molotov", InReserve)
            ),
            numOfItemsCanCarry = 2,
            clock = fixedClock
        )
        val woundedSurvivor = survivor.applyWound()
        woundedSurvivor.equippedWith shouldBe listOf(
            Equipment("Baseball bat", InHand), Equipment("Molotov", InReserve)
        )
    }

    @Test
    fun `should gain one experience each time kills a zombie`() {
        val survivor = Survivor("Max Ryder", clock = fixedClock)

        val result = survivor.killZombie()

        result.experience shouldBe 1
    }

    @Test
    fun `should advance ('level up') to level Yellow when exceeds 6 Experience`() {
        val survivor = Survivor("Max Ryder", experience = 6, clock = fixedClock)

        val result = survivor.killZombie()

        result.level shouldBe YELLOW
    }

    @Test
    fun `should advance ('level up') to level Orange when exceeds 18 Experience`() {
        val survivor = Survivor("Max Ryder", experience = 18, clock = fixedClock)

        val result = survivor.killZombie()

        result.level shouldBe ORANGE
    }

    @Test
    fun `should advance ('level up') to level Red when exceeds 42 Experience`() {
        val survivor = Survivor("Max Ryder", experience = 42, clock = fixedClock)

        val result = survivor.killZombie()

        result.level shouldBe RED
    }
}
