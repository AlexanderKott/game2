package site.kotty_kov.Amusement_vo1.funnymath

import kotlin.random.Random


class FunnyMathGen {

    fun genPlusEquation(): LevelFunnyMath {
        val maxDigit = Random.nextInt(1, 10)
        val second = Random.nextInt(0, maxDigit)
        val third =  maxDigit - second
        return LevelFunnyMath(arrayOf(second.digitToChar() ,third.digitToChar(),maxDigit.digitToChar(),'+'  ), arrayOf())
    }

    fun genMinusEquation(): LevelFunnyMath {
        val first = Random.nextInt(1, 9)
        val second = Random.nextInt(first + 1, 10)
        val third =  second - first
        return LevelFunnyMath(arrayOf(second.digitToChar() ,third.digitToChar(),first.digitToChar() ,'–' ), arrayOf())
    }

    private var prevValue  = arrayOf<Char>()

     fun generate(): LevelFunnyMath {
        var step : LevelFunnyMath
        do {
              step = if (Random.nextBoolean()) {
                genPlusEquation()
            } else {
                genMinusEquation()
            }

        } while (step.levelArray.contentEquals(prevValue))
        prevValue = step.levelArray
        return  randomizeCell(step)
    }

    /**
     * Добавить значек вороса к математическому примеру
     * Берется массив с примером, к нему пишится, то число которые "выпало",
     * далее к нему пишится  это самое число, далее пишится само место числа которое выпало.
     * Само выпавшее число заменяется знаком вопрос
     */
    private fun randomizeCell(step: LevelFunnyMath): LevelFunnyMath {
        val cellIndex = Random.nextInt(0, 3)
        val drop = step.levelArray[cellIndex]
        step.answersArray  += drop
        step.answersArray  += converter(cellIndex).digitToChar()
        step.levelArray[cellIndex] = '❓' //
        return step
    }

    private fun converter(i : Int) : Int {
        return when (i){
            0 -> 0
            1 -> 2
            else -> 4
        }
    }


    }


