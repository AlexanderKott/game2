package site.kotty_kov.Amusement_vo1.music

import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.common.Tegg
import site.kotty_kov.Amusement_vo1.databinding.MusicGameBinding

enum class MusicPeriod {
    LISTEN, REPEAT
}


data class AnimDuration(val flyAnimDuration: Long, val disappearAnimDuration: Long)

class MusicGameLayout {

      var places = arrayOf<Point>()
      private set

    fun drawShapesMusic(
        context: Context,
        binding: MusicGameBinding,
        prefix: String,
        imSize: Int,
        leftPadding: Int,
        topPadding: Int,
        x: Int,
        y: Int
    ) {
        var lshift = 0
        var tshift = 0

        val images = arrayOf<Int>(
            R.drawable.ic_arf,
            R.drawable.ic_git,
            R.drawable.ic_piano,
            R.drawable.ic_lir
        )


        for (i in 1..4) {
            val imageV = ImageView(context)
            imageV.tag = Tegg(prefix, i - 1)

            imageV.setImageResource(images[i - 1])
            binding.gameArea.addView(imageV)
            imageV.alpha = 0.75F

            imageV.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(lshift + x, tshift + y, 0, 0)
                height = imSize
                width = imSize

            }

            places += Point(imageV.marginLeft + imSize / 2 , imageV.marginTop - imageV.marginTop / 5)

            lshift += imSize + leftPadding

            if (i % 2 == 0) {
                tshift += imSize + topPadding
                lshift = 0
            }
        }
    }

}
