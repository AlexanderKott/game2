package site.kotty_kov.Amusement_vo1.happycat

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.common.CommonConstants
import site.kotty_kov.Amusement_vo1.common.Tegg
import site.kotty_kov.Amusement_vo1.databinding.CatsGameBinding


fun drawShapes(
    context: Context,
    binding: CatsGameBinding,
    res: Int,
    prefix: String,
    imSize: Int,
    leftPadding: Int,
    topPadding: Int,
    x: Int,
    y: Int,
    backgroundView:  GradientDrawable? = null
) {
    var lshift = 0
    var tshift = 0

    for (i in 1..9) {
        val iv = ImageView(context)
        iv.tag = Tegg(prefix, i - 1)

        backgroundView?.let{
            iv.background = it
        }

        iv.setImageResource(res)
        binding.gameArea.addView(iv)

        iv.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(lshift + x, tshift + y, 0, 0)
            height = imSize
            width = imSize
        }

        lshift += imSize + leftPadding

        if (i.rem(3) == 0) {
            tshift += imSize + topPadding
            lshift = 0
        }
    }
}



fun prepareTile(resources: Resources): GradientDrawable {
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.TL_BR,
        intArrayOf(
            Color.parseColor(CommonConstants.color1),
            Color.parseColor(CommonConstants.color2))
    );

    gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
    gradientDrawable.setGradientRadius(440.0f);
    gradientDrawable.cornerRadius = 0f;

    return gradientDrawable
}




