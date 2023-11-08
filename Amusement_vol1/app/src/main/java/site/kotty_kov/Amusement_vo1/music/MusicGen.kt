package site.kotty_kov.Amusement_vo1.music

import site.kotty_kov.Amusement_vo1.common.LevelGenerator
import site.kotty_kov.Amusement_vo1.common.ResultChecking
import site.kotty_kov.Amusement_vo1.common.Step
import kotlin.random.Random

class MusicGen : LevelGenerator  {
    override fun checkType() = ResultChecking.BY_ORDER
    override fun noRecurringInput() = false

    private var arrayOfNotes = arrayOf<Int>()

    override fun doGeneratorReset() {
        arrayOfNotes = arrayOf<Int>()
    }

    override fun generate(): Step {
        var nextDigit = Random.nextInt(0, 4)

        if (arrayOfNotes.size > 2 && arrayOfNotes[arrayOfNotes.size - 1] ==
            nextDigit && arrayOfNotes[arrayOfNotes.size - 2] == nextDigit) {
            while (arrayOfNotes[arrayOfNotes.size - 1] == nextDigit) {
                nextDigit = Random.nextInt(0, 4)
            }
        }

        arrayOfNotes += nextDigit
        return Step(arrayOfNotes, arrayOfNotes)
    }
}


