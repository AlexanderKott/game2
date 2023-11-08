package site.kotty_kov.Amusement_vo1.happycat

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import site.kotty_kov.Amusement_vo1.common.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToLong


class HappyCatLogic(val storage: Storage, val generator: LevelGenerator) {

    val CURTAIN = "curtain"
    val CELL = "cell"

    val debugOutput = MutableLiveData<String>()

    val endOfGame = MutableLiveData<GameResult?>()
    val timer = MutableLiveData<String>()
    val animWidthConfiguration = MutableLiveData<Float>()
    val screenConfig = MutableLiveData<ScreenConfig>()

    //game process
    val closeBeforeNewStep = MutableLiveData<Array<Int>>()
    val feedBackForUsersTap = MutableLiveData<Pair<SubStepResult, Int>>()
    val stepFeedBack = MutableLiveData<Boolean>()
    val fillInNewStep = MutableLiveData<Array<Int>>()


    //in
    private var stepTime = 0L // 5000L  // 1300L - possible minimum
    private var howManyToPlay: Long = 0L //35 * 1000L // время будет округлено
    private var animDuration = 0L //700

    var sport: Boolean = false

    //inner vars
    private lateinit var stepsQuantity: AtomicLong

    private var gameplayHandler = Handler(Looper.getMainLooper())

    private var timeDisplay = Handler(Looper.getMainLooper())
    private var displayedTime: AtomicLong = AtomicLong(0)

    private var score: AtomicInteger = AtomicInteger(0)
    private var stepsCount: AtomicInteger = AtomicInteger(0)
    private var penalty: AtomicInteger = AtomicInteger(0)

    private var playerInput = emptyArray<Int>()
    private var rightAnswer = emptyArray<Int>()
    private var newLevel = emptyArray<Int>()

    private var canPlay: AtomicBoolean = AtomicBoolean(false)
    private var thisFirstStep: AtomicBoolean = AtomicBoolean(true)

    private lateinit var checkResult: ResultChecking
    private var uniqPlayerInput: Boolean = true

    private var allowedFailPercent = 0 //OFF
    private var scoreMultiplier = 0   //OFF


    private var startTime: Long = 0
    private var estimatedTime: Long = 0


    fun Int.percent(percent: Int) = this * percent / 100

    fun setupScreen(config: ScreenConfig) {
        animWidthConfiguration.value =
            config.scrHeight.percent(config.cellWandHPercents).toFloat() / 2
        screenConfig.value = ScreenConfig(
            scrHeight = config.scrHeight,
            cellWandHPercents = config.scrHeight.percent(config.cellWandHPercents),
            gapWandHPercents = config.scrHeight.percent(config.gapWandHPercents),
        )
    }

    fun setupGame(config: GameConfig) {
        checkResult = generator.checkType()
        uniqPlayerInput = generator.noRecurringInput()

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
            closeBeforeNewStep.value = newLevel // Just close

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
                hours, minutes, seconds,
                thisIsNewRecord, prevRecordSport
            )


        } else {
            //был ли рекорд в обычном режиме

            val failPercent = penalty.get().toFloat() * 100.toFloat() / stepsCount.get()

            //  Log.e ("exc", "failPercent ${failPercent}   allowedFailPercent${allowedFailPercent}" )
            if (failPercent <= allowedFailPercent) { //фейлов не больше чем разрешено
                if (score.get() * scoreMultiplier > prevRecordNormal) { //не менее предыдущего рекорда
                    gameResult = EndOfGame.NEWRECORD
                }
            }

            GameResult(
                gameResult,
                score.get(), scoreMultiplier,
                0, 0, 0,
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
                playerInput = arrayOf()

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
            playerInput = arrayOf()
        }, (animDuration * 0.35).toLong())
    }


    private fun generateAndOpenDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            generateStep()
            fillInNewStep.value = newLevel
        }, animDuration)
    }

    private fun startGameplayDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            canPlay.set(true)
        }, animDuration * 2)
    }


    private fun checkPlayersTurn(informPlayer: Boolean) {
        stepsCount.getAndIncrement()

        if (checkResult == ResultChecking.BY_CONTENT) {
            playerInput.sort()
            rightAnswer.sort()
        }
        if (playerInput.contentEquals(rightAnswer)) {
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
        rightAnswer = step.answersArray
    }


    fun startGame() {
        selfCheck()
        gameplayHandler.removeCallbacks(step)
        timeDisplay.removeCallbacks(timeDisplayTick)
        stepsCount.set(0)
        penalty.set(0)
        score.set(0)
        rightAnswer = arrayOf()
        playerInput = arrayOf()
        stepsQuantity = getStepsQuantity()
        displayedTime.set(getStepsInSeconds())
        thisFirstStep.set(true)
        startTime = System.currentTimeMillis()
        timeDisplay.postDelayed(timeDisplayTick, animDuration + 1000)
        gameplayHandler.post(step)
    }


    fun getPlayerClick(value: Int) {
        if (canPlay.get()) {
            if ((playerInput.contains(value)) && !uniqPlayerInput) {
                return
            }
            playerInput += value

            if (checkResult == ResultChecking.BY_CONTENT) {
                if (rightAnswer.contains(value)) {
                    feedBackForUsersTap.value = SubStepResult.GOOD to value //Good
                } else {
                    feedBackForUsersTap.value = SubStepResult.FAIL to value //fail
                }
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

