package site.kotty_kov.Amusement_vo1.music

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import site.kotty_kov.Amusement_vo1.common.*


class MusicLogic(private val storage: Storage,
                 private val generator: LevelGenerator) {

    private var playerInput = emptyArray<Int>()
    private var rightAnswer = emptyArray<Int>()
    private var newLevel = emptyArray<Int>()

    val screenConfig = MutableLiveData<ScreenConfig>()
    val endOfGame = MutableLiveData<GameResult>()
    val animationsTime = MutableLiveData<AnimDuration>()

    val debugOutput = MutableLiveData<String>()
    val inform = MutableLiveData<MusicPeriod>()

    val note = MutableLiveData<Int>()

    var stepTime = 1200L

    private var sportMode = true

    fun Int.percent(percent: Int) = this * percent / 100

    fun setupGame(config: GameConfig) {
        sportMode = config.sportMode
        stepTime = config.cycleDuration
        storage.setScoreMultiplier(config.scoreMultiplier.toString())
    }


    fun setupScreen(config: ScreenConfig) {
        screenConfig.value  = ScreenConfig(
            scrHeight = config.scrHeight,
            cellWandHPercents = config.scrHeight.percent(config.cellWandHPercents),
            gapWandHPercents = config.scrHeight.percent(config.gapWandHPercents),
        )

      //  val firstAnim = stepTime.toInt().percent(80).toLong()
     //   val secondAnim = stepTime.toInt().percent(19).toLong()
        animationsTime.value = AnimDuration(1200, 800)
    }


    fun loopRun() {
        val handler = Handler(Looper.getMainLooper())
        inform.value = MusicPeriod.LISTEN
        var arrayStep = 0
        val r: Runnable = object : Runnable {
            override fun run() {
                note.value = newLevel[arrayStep]
                arrayStep++
                if (arrayStep != newLevel.size) {
                    handler.postDelayed(this, stepTime)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        inform.value = MusicPeriod.REPEAT
                      }, stepTime)

                }
            }
        }
        handler.postDelayed(r, stepTime)
    }





    fun usersAnswer(i: Int) {
        if (inform.value == MusicPeriod.LISTEN) return

        playerInput += i

        if (playerInput.size == newLevel.size) {
          if (playerInput.contentEquals(newLevel)){
              startGameTurn()
          } else if (newLevel.size > storage.getPreviousSportRecord()){
                storage.saveGameResult(sportMode, newLevel.size -1)
                endOfGame.value =

              GameResult(
                  EndOfGame.NEWRECORD,
                  newLevel.size -1, 1,
                  0,0, 0,
                  true, 0
              )

            } else {
                endOfGame.value =
              GameResult(
                  EndOfGame.FAIL,
                  newLevel.size -1, 1,
                  0,0, 0,
                  false, 0
              )

            }

        }
    }

    fun startGameTurn() {
        playerInput = emptyArray()
        generateNextTurn()
        loopRun()
    }

    fun generateNextTurn() {
        val step = generator.generate()
        newLevel = step.levelArray
        debugOutput.value = "expect: ${step.levelArray.joinToString()}"
        rightAnswer = step.answersArray
    }

    fun startGame() {
        playerInput = emptyArray()
        rightAnswer = emptyArray<Int>()
        newLevel = emptyArray<Int>()
        generator.doGeneratorReset()
        startGameTurn()

    }

}