package site.kotty_kov.Amusement_vo1.happycat

import site.kotty_kov.Amusement_vo1.common.LevelGenerator
import site.kotty_kov.Amusement_vo1.common.ResultChecking
import site.kotty_kov.Amusement_vo1.common.Step
import kotlin.random.Random


class HappyCatGen : LevelGenerator {

    override fun checkType() = ResultChecking.BY_CONTENT

    override fun doGeneratorReset() {
    }

    override fun noRecurringInput() = true

    override fun generate(): Step {
        val a = ArrayWrapper()
        var arrayOfGeneratedLevel = Array<Int>(9) { 0 }

        var faces = Random.nextInt(5)
        if (faces == 0) {
            faces = if (Math.random() > 0.4) 1 else 0
        }
        if (faces == 1) {
            faces = if (Math.random() > 0.5) 2 else 1
        }
        var skulls = Random.nextInt(5) + 1
        var doorsNempty = arrayOfGeneratedLevel.size - faces - skulls

        if (faces > 0) {
            a.addValues(arrayOfGeneratedLevel, 1, faces)
        }
        a.addValues(arrayOfGeneratedLevel, 2, skulls)
        a.addRandom(arrayOfGeneratedLevel, -1, -2, doorsNempty)

        arrayOfGeneratedLevel.shuffle()

        return Step(arrayOfGeneratedLevel, getCorrectAnswer(arrayOfGeneratedLevel))
    }
}


fun getCorrectAnswer(inc : Array<Int>) : Array<Int>  {
    var temp  = emptyArray<Int>()
    for (i in inc.indices){
        if (inc[i] == 1) {
            temp += i
        }
    }
    return temp
}

class ArrayWrapper {
    private var x = 0
      fun addValues(tempArray: Array<Int>, value : Int, size : Int) {
          val nsize = x + size -1
        for (i in x..nsize ) {
            tempArray[i] = value
            x++
        }
    }

     fun addRandom (tempArray: Array<Int>, value : Int, value2 : Int , size : Int){
         val nsize = x + size -1
         for (i in x..nsize ) {
             tempArray[i] = if (Math.random() > 0.5) value else value2
             x++
         }
     }

}


