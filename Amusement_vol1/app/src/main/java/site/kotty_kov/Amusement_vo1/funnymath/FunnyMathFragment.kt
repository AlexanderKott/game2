package site.kotty_kov.Amusement_vo1.funnymath

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.common.*
import site.kotty_kov.Amusement_vo1.common.di.FunnyMathGame
import site.kotty_kov.Amusement_vo1.databinding.FunnyMathBinding
import javax.inject.Inject

@AndroidEntryPoint
class FunnyMathFragment : Fragment() {

    @FunnyMathGame
    @Inject
    lateinit var sound: GSound
    @FunnyMathGame
    @Inject
    lateinit var model: FunnyMathLogic
    @FunnyMathGame
    @Inject
    lateinit var tale: GradientDrawable
    @FunnyMathGame
    @Inject
    lateinit var scrConfig: ScreenConfigMath


    private var _binding: FunnyMathBinding? = null
    private val binding get() = _binding!!

    private companion object {
        const val BUTTON_LABEL = "button"
        const val PANEL_LABEL = "panel"
        const val QUESTION_SIGN = "\uD83D\uDEAB"
        const val EQUAL_SIGN = "="

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FunnyMathBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val args: FunnyMathFragmentArgs by navArgs()
        val alertDialog = setUpAlertDialog(requireContext()) { model.startGame() }
        model.setupGame(args.gameConfig)
        binding.gameArea.background = tale
        model.setupScreen(scrConfig)
        model.startGame()
        var buttons: Array<MaterialButton>? = null



        model.screenConfig.observe(viewLifecycleOwner) { value ->
            if (value == null) return@observe

            val params: ViewGroup.LayoutParams = binding.gameArea.layoutParams
            params.height = value.scrWidth
            binding.gameArea.layoutParams = params

            drawAnswerButtons(
                requireContext(), binding, BUTTON_LABEL,
                value.answerButtonsSize,
                value.answerButtonsLeftPadding,
                value.answerButtonsTopPadding,
                value.answerButtonsX,
                value.answerButtonsY
            )

            binding.gameArea.children.forEach { it ->
                if (it.tag != null) {
                    if ((it.tag as Tegg).name.contains(BUTTON_LABEL)) {
                        val image = it as MaterialButton
                        image.setOnClickListener {
                            model.getPlayerClick((it.tag as Tegg).value)
                        }
                    }
                }

            }

            buttons = drawPanel(
                requireContext(), binding, PANEL_LABEL,
                value.bigFontSize.toFloat(),
                value.panelSize,
                value.panelLeftPadding,
                value.answerButtonsTopPadding,
                value.panelX,
                value.panelY,
            )

        }

        model.fillInNewStep.observe(viewLifecycleOwner) { gameLevelAndSetups ->
            buttons?.let {
                for (i in 0..4) {
                    it[i].textSize = gameLevelAndSetups.bigFontSize
                }

                it[0].text = gameLevelAndSetups.level.levelArray[0].toString()
                it[1].text = gameLevelAndSetups.level.levelArray[3].toString()
                it[2].text = gameLevelAndSetups.level.levelArray[1].toString()
                it[3].text = EQUAL_SIGN
                it[4].text = gameLevelAndSetups.level.levelArray[2].toString()

                it[gameLevelAndSetups.level.answersArray[0].toInt()].textSize =
                    gameLevelAndSetups.smallFontSize
            }
        }



        model.feedBackForUsersTap.observe(viewLifecycleOwner) { value ->
            buttons?.get(value.position)?.text = value.usersAnswer.toString()
            buttons?.get(value.position)?.textSize = value.bigFontSize

            when (value.result) {
                SubStepResult.FAIL -> {
                    buttons?.get(3)?.textSize = value.smallFontSize
                    buttons?.get(3)?.text = QUESTION_SIGN
                }
                SubStepResult.GOOD -> {
                    sound.beep()
                }
            }
        }

        model.timer.observe(viewLifecycleOwner) { value ->
            binding.time.text = value.toString()
        }


        model.endOfGame.observe(viewLifecycleOwner) { endOfGame ->
            endOfGame?.let {
                if (endOfGame.result == EndOfGame.NEWRECORD) {
                    alertDialog.setTitle(getString(R.string.recordTellYourFriends))
                } else {
                    alertDialog.setTitle(getString(R.string.tryAgain))
                }


                alertDialog.prepareAndShowDialogMessage(endOfGame, requireContext())
            }
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}