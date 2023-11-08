package site.kotty_kov.Amusement_vo1.common.levelSetups

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.common.CommonConstants
import site.kotty_kov.Amusement_vo1.common.GameConfig
import site.kotty_kov.Amusement_vo1.common.Storage
import site.kotty_kov.Amusement_vo1.common.gradientBackground
import site.kotty_kov.Amusement_vo1.databinding.LevelFragmentBinding


@AndroidEntryPoint
class LevelFragment : Fragment() {

    private var _binding: LevelFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var config: GameConfig

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LevelFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val args: LevelFragmentArgs by navArgs()
        val model = LevelModel()

        createDifficultyRadiobuttons(model, args)
        preapreGoalInfo(model, args)
        prepareStartButton(args)

        gradientBackground(binding.gradientPreloader.background  as GradientDrawable)


        return root
    }





    private fun prepareStartButton(args: LevelFragmentArgs) {
        binding.start.setOnClickListener {
            val destinationCat =
                LevelFragmentDirections.actionLevelFragmentToCatsGameFragment(config)
            val destinationMusic =
                LevelFragmentDirections.actionLevelFragmentToMusicGameFragment(config)
            val destinationMath =
                LevelFragmentDirections.actionLevelFragmentToFunnyMathFragment(config)

            val destontion = when (args.gameName) {
                CommonConstants.CATS_GAME -> destinationCat
                CommonConstants.MUSIC_GAME -> destinationMusic
                CommonConstants.MATH_GAME -> destinationMath
                else -> throw Exception("unsupported game")
            }

            NavHostFragment.findNavController(this)
                .navigate(destontion)

        }
    }

    private fun preapreGoalInfo(
        model: LevelModel,
        args: LevelFragmentArgs
    ) {
        val gGoal = model.gameGoal[args.gameName] ?: 0
        val goalArray = resources.getStringArray(R.array.game_goal)
        binding.goal.text = goalArray[gGoal]
    }

    private fun createDifficultyRadiobuttons(
        model: LevelModel,
        args: LevelFragmentArgs
    ) {
        val difficultyArray = resources.getStringArray(R.array.level_array)
        val difficulty = model.difficultyLevels[args.gameName]

        var setDifficultyAutomatically = true
        difficulty?.let {
            for (i in difficulty.indices) {
                if (difficulty[i] == null) continue

                val radioButton = RadioButton(requireContext())
                radioButton.text = difficultyArray[i]
                radioButton.id = i
                radioButton.setOnClickListener {
                    difficulty[i]?.let { config = it }
                    val recordStorage =
                        Storage("${args.gameName}", requireContext())
                     recordStorage.setScoreMultiplier("${config.scoreMultiplier}")

                    val record = if (config.sportMode) recordStorage.getPreviousSportRecord()
                    else recordStorage.getPreviousNormalRecord()

                    binding.record.text =  if (record == 0) getString(R.string.recordIsNotSet)
                    else getString(R.string.yourRecordIs, record)
                }



                if (setDifficultyAutomatically) {
                    difficulty[i]?.let { config = it }
                    radioButton.isChecked = true
                    setDifficultyAutomatically = false
                }

                binding.dificultyLevel.addView(radioButton)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}