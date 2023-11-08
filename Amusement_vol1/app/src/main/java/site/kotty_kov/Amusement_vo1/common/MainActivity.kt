package site.kotty_kov.Amusement_vo1.common

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.databinding.ActivityMainBinding
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private lateinit var mediaPlayer : MediaPlayer

    private lateinit var  navHostFragment : NavHostFragment

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.music)

          navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_main_menu
        ) as NavHostFragment

        setupActionBarWithNavController(navHostFragment.navController)

        mediaPlayer.start()

        viewModel.music.observe(this, Observer { musicMode ->
           // if (mediaPlayer.isPlaying)  { mediaPlayer.pause() } else {mediaPlayer.start()}
            if (musicMode) {mediaPlayer.start()} else {mediaPlayer.pause()}
        })

    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.pause()
        viewModel.pause()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp()
    }

}



