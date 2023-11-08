package site.kotty_kov.Amusement_vo1.music

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.common.*
import site.kotty_kov.Amusement_vo1.common.di.MusicGame
import site.kotty_kov.Amusement_vo1.databinding.MusicGameBinding
import javax.inject.Inject


@AndroidEntryPoint
class MusicGameFragment : Fragment() {
    @MusicGame
    @Inject
    lateinit var model: MusicLogic
    @MusicGame
    @Inject
    lateinit var scrConfig: ScreenConfig

    private var _binding: MusicGameBinding? = null
    private val binding get() = _binding!!
    private var anim : AnimatorSet? = null

    private companion object {
        val INSTRUMENT_LABEL = "instrument"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MusicGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val args: MusicGameFragmentArgs by navArgs()
        val alertDialog= setUpAlertDialog(requireContext()) { model.startGame() }
        val gameLayout = MusicGameLayout()

        model.setupGame(args.gameConfig)
        model.setupScreen(scrConfig)
        prepareAnimations()

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

        model.screenConfig.observe(viewLifecycleOwner) { value ->
            if (value == null) return@observe

            val params: ViewGroup.LayoutParams = binding.gameArea.layoutParams
            params.height = value.scrHeight
            binding.gameArea.layoutParams = params

            gameLayout.drawShapesMusic(requireContext(), binding,
                INSTRUMENT_LABEL,
                value.cellWandHPercents,
                value.gapWandHPercents,
                value.gapWandHPercents,
                value.gapWandHPercents,
                value.gapWandHPercents)

        binding.gameArea.children.forEach { it ->
            if (it.tag != null) {
                if ((it.tag as Tegg).name == INSTRUMENT_LABEL ) {
                    val image = it as ImageView
                    image.setOnClickListener {
                        PlayNote().play((it.tag as Tegg).value)
                        model.usersAnswer((it.tag as Tegg).value)
                    }
                }
            }
          }

        }


        model.note.observe(viewLifecycleOwner) { digit ->

            binding.noteImg.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(gameLayout.places[digit].x, gameLayout.places[digit].y, 0, 0)
            }
            anim?.start()
            PlayNote().play(digit)
        }


        model.inform.observe(viewLifecycleOwner) { period ->
            binding.display.text = when (period){
                MusicPeriod.REPEAT -> getString(R.string.repaetMe)
                MusicPeriod.LISTEN -> getString(R.string.listenToMe)
            }
        }

        model.startGameTurn()
        return root
    }




    private fun prepareAnimations() {
            model.animationsTime.observe(viewLifecycleOwner) { time ->
            val transX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0F, 100F)
            val transY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 220F, 0F)
            val scale = PropertyValuesHolder.ofFloat(View.SCALE_X, 0F, 1F)
            val alfa = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5F, 1F)

            val animatorAppear =
                ObjectAnimator.ofPropertyValuesHolder(binding.noteImg, transX, transY, scale, alfa)
                    .apply {
                        duration =  time.flyAnimDuration
                        repeatCount = 0
                        interpolator = LinearInterpolator()
                    }


            val alfaDis = PropertyValuesHolder.ofFloat(View.ALPHA, 1F, 0F)

            val animatorDisappear =
                ObjectAnimator.ofPropertyValuesHolder(binding.noteImg, alfaDis)
                    .apply {
                        duration = time.disappearAnimDuration
                        repeatCount = 0
                        interpolator = LinearInterpolator()
                    }


             anim = AnimatorSet()
             anim?.playSequentially(animatorAppear, animatorDisappear)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}