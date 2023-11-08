package site.kotty_kov.Amusement_vo1.funnymath

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import site.kotty_kov.Amusement_vo1.common.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToLong


class FunnyMathLogic(val storage: Storage, val generator: FunnyMathGen) {

    val debugOutput = MutableLiveData<String>()

    val endOfGame = MutableLiveData<GameResult?>()
    val timer = MutableLiveData<String>()
    val screenConfig = MutableLiveData<ScreenConfigMath>()

    //game process
    val closeBeforeNewStep = MutableLiveData<Boolean>()
    val feedBackForUsersTap = MutableLiveData<TurnResult>()
    val stepFeedBack = MutableLiveData<Boolean>()
    val fillInNewStep = MutableLiveData<LevelAndSetUps>()


    //in
    private var stepTime = 0L // 5000L  // 1300L - possible minimum
    private var howManyToPlay: Long = 0L //35 * 1000L // время будет округлено
    private var animDuration = 0L //700

    var sport: Boolean = false

    //inner vars
    private lateinit var stepsQuantity: AtomicLong
    private var bigFontSize =  0F
    private var gameplayHandler = Handler(Looper.getMainLooper())

    private var timeDisplay = Handler(Looper.getMainLooper())
    private var displayedTime: AtomicLong = AtomicLong(0)

    private var score: AtomicInteger = AtomicInteger(0)
    private var penalty: AtomicInteger = AtomicInteger(0)

    private var playerInput = -1
    private var rightAnswer: TurnResult? = null
    private var newLevel = emptyArray<Char>()

    private var canPlay: AtomicBoolean = AtomicBoolean(false)
    private var thisFirstStep: AtomicBoolean = AtomicBoolean(true)
    private var stepsCount: AtomicInteger = AtomicInteger(0)

    private var allowedFailPercent = 0 //OFF
    private var scoreMultiplier = 0   //OFF


    private var startTime: Long = 0
    private var estimatedTime: Long = 0

    private var smallFontSizeL = 10F

    fun Int.percent(percent: Int) = this * percent / 100

    fun setupScreen(config: ScreenConfigMath) {
        screenConfig.value = ScreenConfigMath(
            scrWidth = config.scrWidth,
            answerButtonsSize = config.scrWidth.percent(config.answerButtonsSize),
            answerButtonsLeftPadding = config.scrWidth.percent(config.answerButtonsLeftPadding),
            answerButtonsTopPadding = config.scrWidth.percent(config.answerButtonsTopPadding),
            answerButtonsX = config.scrWidth.percent(config.answerButtonsX),
            answerButtonsY = config.scrWidth.percent(config.answerButtonsY),

            panelSize = config.scrWidth.percent(config.panelSize),
            panelLeftPadding = config.scrWidth.percent(config.panelLeftPadding),
            panelTopPadding = config.scrWidth.percent(config.panelTopPadding),
            panelX = config.scrWidth.percent(config.panelX),
            panelY = config.scrWidth.percent(config.panelY),
            0,
            bigFontSize = config.scrWidth.percent(config.bigFontSize)
          )

        bigFontSize  = config.scrWidth.percent(config.bigFontSize).toFloat()
        smallFontSizeL = config.scrWidth.percent(config.smallFontSize).toFloat()


    }

    fun setupGame(config: GameConfig) {
        animDuration = config.animDuration
        sport = config.sportMode
        stepTime = config.cycleDuration
        howManyToPlay = config.gameDuration
        allowedFailPercent = config.allowedFailPercentNormalModeToWin
        scoreMultiplier = config.scoreMultiplier
        storage.setScoreMultiplier(config.scoreMultiplier.toString())
    }


//----------------------------


    private val timeDisplayTick: Runnable = object : Runnable {
        override fun run() {
            if (stepsQuantity.get() > 0) {
                timeDisplay.postDelayed(this, 1000)
            }
            timer.value = "${displayedTime.getAndDecrement()}"
        }
    }


    private val step: Runnable = object : Runnable {
        override fun run() {
            if (thisFirstStep.get()) {
                canPlay.set(false)
                generateAndOpenDelay()
                startGameplayDelay()
                thisFirstStep.set(false)
                gameplayHandler.postDelayed(this, stepTime)
                return
            }

            if (!sport) {
                stepsQuantity.getAndDecrement()
                displayedTime.set(getStepsInSeconds())
            }
            closeBeforeNewStep.value = true // Just close

            //sport mode
            if (sport) {
                lateActions()
                return
            }
            //normal mode
            if (stepsQuantity.get() > 0) {
                stopGamePlayAndCheckResultsDelay()
                generateAndOpenDelay()
                startGameplayDelay()
                gameplayHandler.postDelayed(this, stepTime)
            } else {
                canPlay.set(false)
                checkPlayersTurn(false)
                debugOutput.value = "NORMAL FINISH score: $score  penalty: $penalty"
                val res = prepareResults(
                    storage.getPreviousSportRecord(),
                    storage.getPreviousNormalRecord()
                )
                endOfGame.value = res
                if (res.result == EndOfGame.NEWRECORD) {
                    storage.saveGameResult(sport, res.scores * res.multiplyer)
                }
            }
        }
    }

    fun prepareResults(prevRecordSport: Int, prevRecordNormal: Int): GameResult {
        var gameResult = EndOfGame.FAIL

        return if (sport) {
            if (score.get() * scoreMultiplier > prevRecordSport) {
                gameResult = EndOfGame.NEWRECORD
            }

            val seconds = (estimatedTime / 1000).toInt() % 60
            val minutes = (estimatedTime / (1000 * 60) % 60)
            val hours = (estimatedTime / (1000 * 60 * 60) % 24)

            var thisIsNewRecord = false
            if (gameResult == EndOfGame.NEWRECORD) {
                thisIsNewRecord = true
            }


            GameResult(
                         gameResult,
                         score.get(), scoreMultiplier,
                         hours,minutes, seconds,
                         thisIsNewRecord, prevRecordSport
            )
        } else {
            //шагов всего
            //количество успешных
            //количество фейлов

            val failPercent = penalty.get().toFloat() * 100.toFloat() / stepsCount.get()
           // Log.e ("exc", "failPercent ${failPercent}  penalty ${penalty.get()}  stepsCount ${stepsCount.get()}        allowedFailPercent${allowedFailPercent}" )
            if (failPercent <= allowedFailPercent) {
                if (score.get() * scoreMultiplier > prevRecordNormal) {
                    gameResult = EndOfGame.NEWRECORD
                }
            }

            GameResult(
                gameResult,
                score.get(), scoreMultiplier,
                0,0, 0,
                false, prevRecordSport
            )
        }
    }


    private fun lateActions() {
        Handler(Looper.getMainLooper()).postDelayed({
            stepsQuantity.getAndDecrement()
            canPlay.set(false)
            checkPlayersTurn(true)
            displayedTime.set(getStepsInSeconds())

            if (stepsQuantity.get() > 0) {
                playerInput = -1

                generateAndOpenDelay()
                startGameplayDelay()
                gameplayHandler.postDelayed(step, stepTime)
            } else {
                debugOutput.value = "SPORT FINISH score: $score  penalty: $penalty"
                estimatedTime = System.currentTimeMillis() - startTime

                val results = prepareResults(
                    storage.getPreviousSportRecord(),
                    storage.getPreviousNormalRecord()
                )
                endOfGame.value = results
                if (results.result == EndOfGame.NEWRECORD) {
                    storage.saveGameResult(sport, results.scores * results.multiplyer)
                }
            }
        }, (animDuration * 0.5).toLong())
    }


    private fun stopGamePlayAndCheckResultsDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            canPlay.set(false)
            checkPlayersTurn(true)
            playerInput = -1
        }, (animDuration * 0.35).toLong())
    }


    private fun generateAndOpenDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            generateStep()
            val position = rightAnswer?.position?.toChar() ?: '0'

            fillInNewStep.value =  LevelAndSetUps(LevelFunnyMath( newLevel, arrayOf(position)), bigFontSize, smallFontSizeL)
        }, animDuration)
    }

    private fun startGameplayDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            canPlay.set(true)
        }, animDuration * 2)
    }


    private fun checkPlayersTurn(informPlayer: Boolean) {

        stepsCount.getAndIncrement()

        if (playerInput == rightAnswer?.rightDigit && playerInput != -1) {
            if (informPlayer) {
                stepFeedBack.value = true // send good!
            }
            if (sport) {
                stepsQuantity.getAndAdd(1)
            }
            score.getAndIncrement()
        } else {
            penalty.getAndIncrement()
        }

    }


    fun generateStep() {
        val step = generator.generate()
        newLevel = step.levelArray
        debugOutput.value = "expect: ${step.answersArray.joinToString()}"

        rightAnswer = TurnResult(
            rightDigit = step.answersArray[0].digitToInt(),
            position = step.answersArray[1].digitToInt(),
            usersAnswer = -1,
            null,
            smallFontSizeL,
            bigFontSize
        )
    }


    fun startGame() {
        selfCheck()
        gameplayHandler.removeCallbacks(step)
        timeDisplay.removeCallbacks(timeDisplayTick)
        stepsCount.set(0)
        penalty.set(0)
        score.set(0)
        rightAnswer = null
        playerInput = -1
        stepsQuantity = getStepsQuantity()
        displayedTime.set(getStepsInSeconds())
        thisFirstStep.set(true)
        startTime = System.currentTimeMillis()
        timeDisplay.postDelayed(timeDisplayTick, animDuration + 1000)
        gameplayHandler.post(step)
    }


    fun getPlayerClick(value: Int) {
        if (canPlay.get()) {

            playerInput = value

            if (rightAnswer?.rightDigit == playerInput) {
                feedBackForUsersTap.value =
                    TurnResult(
                        rightAnswer?.rightDigit ?: 0,
                        rightAnswer?.position ?: 0,
                        value,
                        SubStepResult.GOOD,
                        smallFontSizeL,
                        bigFontSize
                    )
            } else {
                feedBackForUsersTap.value =
                    TurnResult(
                        rightAnswer?.rightDigit ?: 0,
                        rightAnswer?.position ?: 0,
                        value,
                        SubStepResult.FAIL,
                        smallFontSizeL,
                        bigFontSize
                    )
            }

        }
    }


    private fun getStepsQuantity() =
        AtomicLong((howManyToPlay.toFloat() / stepTime.toFloat()).roundToLong())

    private fun getStepsInSeconds() = (stepsQuantity.get() * stepTime / 1000)

    private fun selfCheck() {
        if (scoreMultiplier <= 0 || animDuration <= 0L || howManyToPlay <= 0L
            || stepTime <= 0L
        ) {
            throw Exception("Init game first!")
        }
        if (stepTime <= animDuration * 2 || allowedFailPercent < 0 || allowedFailPercent > 100) {
            throw Exception("Wrong game config!")
        }
    }

    fun getAnimDuration() = animDuration
}

