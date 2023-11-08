package site.kotty_kov.Amusement_vo1.common.levelSetups

import site.kotty_kov.Amusement_vo1.common.CommonConstants
import site.kotty_kov.Amusement_vo1.common.GameConfig

class LevelModel {

    private val levelHappyCat : Array<GameConfig?> = arrayOf(
        GameConfig(60000L, 4000L, 400L, false, 1, 100),
        GameConfig(60000L, 3000L,400L, false,5,90),
        GameConfig(60000L, 2500L, 400L, false, 10, 80),
        GameConfig(60000L, 2000L, 400L, false, 20, 40),
        GameConfig(60000L, 1900L, 400L, false, 50, 10),
        GameConfig(3000L, 1600L, 400L, true, 100, 10),
    )

    private val levelMusic = arrayOf(
        null,
        null,
        GameConfig(60000L, 1200L, 1200L, true, 10, 100),
    )

   private val levelFunnyMath : Array<GameConfig?> = arrayOf(

        GameConfig(60000L, 6000L, 400L, false, 1, 100),
        null,
        GameConfig(60000L, 5000L, 400L, false, 10, 100),
        GameConfig(60000L, 4000L, 400L, false, 100, 100),
        GameConfig(60000L, 3000L, 400L, false, 500, 100),
        GameConfig(6000L, 3500L, 400L, true, 1000, 100),
    )

    val difficultyLevels = mutableMapOf<String, Array<GameConfig?>>()
    val gameGoal = mutableMapOf<String, Int>()

    init {
        difficultyLevels[CommonConstants.CATS_GAME] = levelHappyCat
        gameGoal[CommonConstants.CATS_GAME] = 0

        difficultyLevels[CommonConstants.MUSIC_GAME] = levelMusic
        gameGoal[CommonConstants.MUSIC_GAME] = 1

        difficultyLevels[CommonConstants.MATH_GAME] = levelFunnyMath
        gameGoal[CommonConstants.MATH_GAME] = 2
    }


}