package site.kotty_kov.Amusement_vo1.common

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import dagger.hilt.android.scopes.FragmentScoped
import site.kotty_kov.Amusement_vo1.R
import javax.inject.Inject


class GSound (context: Context) {
   private val attrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private val sp = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(attrs)
        .build()

    private val sound : Int = sp.load(context, R.raw.cbell, 1);


   fun beep(){
       sp.play(sound, 1F, 1F, 1, 0, 1.0F);
   }

}