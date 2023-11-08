package site.kotty_kov.Amusement_vo1.funnymath

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import com.google.android.material.button.MaterialButton
import site.kotty_kov.Amusement_vo1.R
import site.kotty_kov.Amusement_vo1.common.CommonConstants
import site.kotty_kov.Amusement_vo1.common.SubStepResult
import site.kotty_kov.Amusement_vo1.common.Tegg
import site.kotty_kov.Amusement_vo1.databinding.FunnyMathBinding


data class LevelFunnyMath(val levelArray: Array<Char>, var answersArray: Array<Char>)
data class TurnResult(var rightDigit : Int,
                      var position : Int,
                      var usersAnswer : Int = -1,
                      var result :SubStepResult? = null,
                      var smallFontSize : Float,
                      var bigFontSize : Float )


data class LevelAndSetUps(val level : LevelFunnyMath, val bigFontSize : Float, val smallFontSize : Float)

data class ScreenConfigMath(val scrWidth : Int,
                            val answerButtonsSize : Int,
                            val answerButtonsLeftPadding : Int,
                            val answerButtonsTopPadding : Int,
                            val answerButtonsX : Int,
                            val answerButtonsY : Int,

                            val panelSize : Int,
                            val panelLeftPadding : Int,
                            val panelTopPadding : Int,
                            val panelX : Int,
                            val panelY : Int,

                            val smallFontSize : Int,
                            val bigFontSize : Int,

                            )

fun drawAnswerButtons(
    context: Context,
    binding: FunnyMathBinding,
    prefix: String,
    imSize: Int,
    leftPadding: Int,
    topPadding: Int,
    x: Int,
    y: Int,
) {
    var lshift = 0
    var tshift = 0

    for (i in 1..10) {
        val button = MaterialButton(context, null, R.attr.materialButtonOutlinedStyle)
        button.text = "${i-1}"
        button.textSize = 23F
        button.tag = Tegg(prefix, i - 1)
        button.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
        button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray));
        binding.gameArea.addView(button)

        button.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(lshift + x, tshift + y, 0, 0)
            height = imSize
            width = imSize
        }

        lshift += imSize + leftPadding

        if (i.rem(5) == 0) {
            tshift += imSize + topPadding
            lshift = 0
        }
    }
}



object PrepareTile {

fun prepare(): GradientDrawable {
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
}


fun drawPanel(
    context: Context,
    binding: FunnyMathBinding,
    prefix: String,
    fontSize : Float,
    imSize: Int,
    leftPadding: Int,
    topPadding: Int,
    x: Int,
    y: Int,
) :  Array<MaterialButton> {
    var lshift = 0
    var tshift = 0
    var buttons = arrayOf<MaterialButton>()


    for (i in 1..5) {
        val button = MaterialButton(context, null,R.attr.materialButtonOutlinedStyle )
        button.text = ""
        button.textSize = fontSize
        button.cornerRadius = 0
        button.insetTop = 0
        button.insetBottom = 0
        button.strokeWidth = 0
        button.isClickable = false
        //button.setPadding(0,0,0,100)

        val paint: TextPaint = button.paint
        var widthX = paint.measureText(CommonConstants.measureText)

        val textShader: Shader = LinearGradient(
            0F, 0F, widthX, button.getTextSize(), intArrayOf(
                Color.parseColor(CommonConstants.colorT1),
                Color.parseColor(CommonConstants.colorT2),
                Color.parseColor(CommonConstants.colorT3),
                Color.parseColor(CommonConstants.colorT4),
                Color.parseColor(CommonConstants.colorT5)

            ), null, Shader.TileMode.CLAMP
        )


        button.getPaint().setShader(textShader)

        button.tag = Tegg(prefix, i - 1)

        binding.gameArea.addView(button)
        buttons +=(button)

        button.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(lshift + x, tshift + y, 0, 0)
            height = imSize
            width = imSize
        }

        lshift += imSize + leftPadding

        if (i.rem(5) == 0) {
            tshift += imSize + topPadding
            lshift = 0
        }
    }

    return buttons
}
