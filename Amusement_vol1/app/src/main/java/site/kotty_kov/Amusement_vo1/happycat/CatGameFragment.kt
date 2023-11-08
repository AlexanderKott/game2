package site.kotty_kov.Amusement_vo1.happycat

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.Provides

import dagger.hilt.android.AndroidEntryPoint
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.common.*
import site.kotty_kov.Amusement_vo1.common.di.HappyCatGame
import site.kotty_kov.Amusement_vo1.databinding.CatsGameBinding
import site.kotty_kov.Amusement_vo1.funnymath.PrepareTile
import javax.inject.Inject

@AndroidEntryPoint
class CatsGameFragment : Fragment() {

    @HappyCatGame
    @Inject
    lateinit var model: HappyCatLogic

    @HappyCatGame
    @Inject
    lateinit var sound: GSound

    @HappyCatGame
    @Inject
    lateinit var scrConfig: ScreenConfig


    private var _binding: CatsGameBinding? = null

    private val binding get() = _binding!!

    private var closeCurtainsAnims = arrayOf<ObjectAnimator>()
    private var openCurtainsAnims = arrayOf<ObjectAnimator>()
    private var cells = arrayOf<ImageView>()

    private companion object {
        const val CELL = "cell"
        const val BACKGROUND = "background"
        const val CURTAIN = "curtain"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CatsGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val args: CatsGameFragmentArgs by navArgs()
        model.setupGame(args.gameConfig)
        model.setupScreen(scrConfig)

        //prepare level

        val curtainAnimDuration = model.getAnimDuration()
        binding.gameArea.background = prepareTile(resources)
        prepareGameField(binding)
        prepareAnimators(binding, curtainAnimDuration)
        val alertDialog = setUpAlertDialog(requireContext()) { model.startGame() }

        //start
        model.startGame()

        //observe
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


        model.stepFeedBack.observe(viewLifecycleOwner) { value ->
            if (value) {
                sound.beep()
            }
        }

        model.debugOutput.observe(viewLifecycleOwner) { value ->
            //binding.display.text = value
        }


        model.timer.observe(viewLifecycleOwner) { value ->
            binding.time.text = value.toString()
        }


        model.closeBeforeNewStep.observe(viewLifecycleOwner) { prevGameLevel ->
            if (prevGameLevel.isEmpty()) { //если это первый уровень который когда либо покаазывался
                closeCurtainsAnims.forEach { it.start() }
            } else {
                closeCurtainsAnims.forEachIndexed { i, anim -> //если уровнь уже был и там нет -1 клеток
                    if (prevGameLevel[i] != -1) {
                        anim.start()
                    }
                }
            }

        }

        model.fillInNewStep.observe(viewLifecycleOwner) { gameLevel ->
            if (gameLevel.isNotEmpty())
                cells.forEachIndexed { i, imageView ->
                    when (gameLevel[i]) {
                        2 -> imageView.setImageResource(R.drawable.new_monster)
                        1 -> imageView.setImageResource(R.drawable.new_cat)
                        -1 -> imageView.setImageResource(R.drawable.closed)
                        -2 -> imageView.setImageResource(R.drawable.new_star)
                    }
                }

            if (gameLevel.isNotEmpty())
                openCurtainsAnims.forEachIndexed { i, it ->
                    if (gameLevel[i] != -1)
                        it.start()
                }
        }



        model.feedBackForUsersTap.observe(viewLifecycleOwner) { value ->
            when (value.first) {
                SubStepResult.GOOD -> cells[value.second].setImageResource(R.drawable.star)
                SubStepResult.FAIL -> cells[value.second].setImageResource(R.drawable.fail)
            }
        }


        return root
    }


    //------------------------------------------------------------------------------


    private fun prepareAnimators(
        binding: CatsGameBinding,
        closeAnim: Long,
    ) {

        model.animWidthConfiguration.observe(viewLifecycleOwner) { value ->
            //close
            val transCl =
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, value, 0F)
            val scaleCl = PropertyValuesHolder.ofFloat(View.SCALE_X, 0F, 1F)

            //open
            val transOp =
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0F, value)
            val scaleOp = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 0F)

            //prepare animations
            //prepare user's click
            binding.gameArea.children.forEach { it ->
                if (it.tag != null) {
                    if ((it.tag as Tegg).name.contains(model.CURTAIN)) {
                        val animatorClose =
                            ObjectAnimator.ofPropertyValuesHolder(it, transCl, scaleCl)
                                .apply {
                                    duration = closeAnim
                                    repeatCount = 0
                                    interpolator = LinearInterpolator()
                                }
                        closeCurtainsAnims += animatorClose

                        val animatorOpen =
                            ObjectAnimator.ofPropertyValuesHolder(it, transOp, scaleOp)
                                .apply {
                                    duration = closeAnim
                                    repeatCount = 0
                                    interpolator = LinearInterpolator()
                                }
                        openCurtainsAnims += animatorOpen


                    } else if ((it.tag as Tegg).name.contains(model.CELL)) {
                        val image = it as ImageView
                        image.setOnClickListener {
                            model.getPlayerClick((it.tag as Tegg).value)
                        }
                        cells += image
                    }
                }
            }
        }
    }


    private fun prepareGameField(binding: CatsGameBinding) {

        model.screenConfig.observe(viewLifecycleOwner) { value ->
            if (value == null) return@observe

            val params: ViewGroup.LayoutParams = binding.gameArea.layoutParams
            params.height = value.scrHeight
            binding.gameArea.layoutParams = params


            val imSize = value.cellWandHPercents
            val leftGap = value.gapWandHPercents
            val topGap = value.gapWandHPercents
            val xOffset = value.gapWandHPercents
            val yOffset = value.gapWandHPercents

            drawShapes(
                requireContext(), binding, R.drawable.cell_background, BACKGROUND,
                imSize,
                leftGap,
                topGap,
                xOffset,
                yOffset,
            )


            drawShapes(
                requireContext(), binding, R.drawable.new_star, CELL,
                imSize,
                leftGap,
                topGap,
                xOffset,
                yOffset,
            )


            drawShapes(
                requireContext(), binding, R.drawable.new_door, CURTAIN,
                imSize,
                leftGap,
                topGap,
                xOffset,
                yOffset,
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}