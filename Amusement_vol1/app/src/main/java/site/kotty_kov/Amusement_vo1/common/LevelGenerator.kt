package site.kotty_kov.Amusement_vo1.common



interface LevelGenerator {
    fun generate() : Step
    fun checkType() : ResultChecking
    fun noRecurringInput() : Boolean
    fun doGeneratorReset()
}