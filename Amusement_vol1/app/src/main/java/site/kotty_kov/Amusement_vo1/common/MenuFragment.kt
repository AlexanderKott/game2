package site.kotty_kov.Amusement_vo1.common

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.databinding.MenuFragmentBinding
import java.util.*


@AndroidEntryPoint
class MenuFragment : Fragment() {

   companion object Constants {
        const val MAX_DELAY = 300
        const val ANIM_DURATION = 10000L
        const val EMPTY_MESSAGE_WHAT = 0x001
    }

    private var  playMusic = true
    private var _binding: MenuFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root




        binding.catsGame.setOnClickListener {
           NavHostFragment.findNavController(this)
                .navigate(MenuFragmentDirections
                    .actionMenuFragmentToLevelFragment(CommonConstants.CATS_GAME))
        }

        binding.musicGame.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(MenuFragmentDirections
                    .actionMenuFragmentToLevelFragment(CommonConstants.MUSIC_GAME))
        }

        binding.funnyMathGame.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(MenuFragmentDirections
                    .actionMenuFragmentToLevelFragment(CommonConstants.MATH_GAME))
        }



        binding.musicBtn.setOnClickListener {
            viewModel.switchONOff()
        }


        binding.about.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("About")
            builder.setMessage(R.string.aboutText)
            builder.setIcon(R.drawable.tent)
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            }

            builder.show()
        }

        //leaves set up
        prepareLeaves()
        //gradient background
        gradientBackground(binding.gradientPreloader.background  as GradientDrawable)

        return root
    }

    private fun prepareLeaves() {
        val wm = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val mDisplaySize: Rect = Rect()
        display.getRectSize(mDisplaySize)
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        var mScale = metrics.density
        Timer().schedule(
            ExeTimerTask(
                MHandler(
                    requireContext(),
                    mDisplaySize,
                    mScale,
                    binding.mainLayout!!
                )
            ), 0, 1500
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }










}

