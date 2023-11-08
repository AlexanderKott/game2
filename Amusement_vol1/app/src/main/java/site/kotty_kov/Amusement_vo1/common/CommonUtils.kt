package site.kotty_kov.Amusement_vo1.common

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.parcelize.Parcelize
import site.kotty_kov.Amusement_vo1.R
import java.util.*


data class Tegg(val name: String, val value: Int)

data class Step (val levelArray : Array<Int>, val answersArray : Array<Int>)

enum class ResultChecking {
    BY_CONTENT, BY_ORDER
}

data class ScreenConfig(val scrHeight : Int,
                        val cellWandHPercents : Int,
                        val gapWandHPercents : Int
)

@Parcelize
data class GameConfig(
    val gameDuration: Long,
    val cycleDuration: Long,
    val animDuration: Long,
    val sportMode: Boolean,
    val scoreMultiplier: Int,
    val allowedFailPercentNormalModeToWin: Int
) : Parcelable

enum class EndOfGame {
    NEWRECORD, FAIL
}

enum class SubStepResult {
    GOOD, FAIL
}



data class GameResult(
    val result: EndOfGame, val scores: Int, val multiplyer: Int,
    val hours: Long, val minutes: Long, val seconds: Int,
    val thisIsNewRecord: Boolean, val prevRecordSport: Int,

    )


fun AlertDialog.prepareAndShowDialogMessage(endOfGame: GameResult, context: Context){
    this.show()

    val hour = if (endOfGame.hours > 0) endOfGame.hours.toString() else "-"
    val min = if (endOfGame.minutes > 0) endOfGame.minutes.toString() else "-"
    val seconds = if (endOfGame.seconds > 0) endOfGame.seconds.toString() else "-"
    val oldRecord = if (endOfGame.prevRecordSport > 0) endOfGame.prevRecordSport.toString() else "-"
    val record = if (endOfGame.thisIsNewRecord) context.getString(R.string.record) else "  :(  "

    val textV = this.findViewById<TextView>(R.id.text_dialog)
    textV?.text = context.getString(R.string.scores,
        endOfGame.scores * endOfGame.multiplyer,
        hour, min, seconds,
        record, oldRecord);

    /*this.setMessage(context.getString(R.string.scores,
        endOfGame.scores * endOfGame.multiplyer,
        hour, min, seconds,
        record, oldRecord)*/
}

fun Fragment.setUpAlertDialog(context : Context, action : ()-> Unit): AlertDialog {

    val factory = LayoutInflater.from(context)
    val aDialogView: View = factory.inflate(R.layout.dialog, null)
    val alertDialog = AlertDialog.Builder(context).create()
    alertDialog.setView(aDialogView)
   // val alertDialog: AlertDialog = AlertDialog.Builder(context).create()

    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.backToMenu)) { dialog, which ->
        NavHostFragment.findNavController(this).navigateUp()
        dialog.dismiss()
    }

    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.retry) ) { dialog, which ->
        action.invoke()
        dialog.dismiss()
    }

    alertDialog.setCanceledOnTouchOutside(false)
    return alertDialog
}

fun gradientBackground(gradient : GradientDrawable){
    //gradient
    val start = Color.parseColor("#FF6093FA");
    val mid = Color.parseColor("#FF153D8C");
    val end = Color.parseColor("#FFB0C7F6");



    val evaluator = ArgbEvaluator()
    val animator = TimeAnimator.ofFloat(0.0f, 1.0f)
    animator.duration = 3500
    animator.repeatCount = ValueAnimator.INFINITE
    animator.repeatMode = ValueAnimator.REVERSE
    animator.addUpdateListener {
        val fraction = it.animatedFraction
        val newStart = evaluator.evaluate(fraction, start, end) as Int
        val newMid = evaluator.evaluate(fraction, mid, start) as Int
        val newEnd = evaluator.evaluate(fraction, end, mid) as Int

        gradient.orientation = GradientDrawable.Orientation.TOP_BOTTOM
        gradient.colors = intArrayOf(newStart, newMid, newEnd)
    }

    animator.start()
}


//----------------------------- leaves

class ExeTimerTask(val mHandler: Handler) : TimerTask() {
    override fun run() {
        mHandler.sendEmptyMessage(MenuFragment.EMPTY_MESSAGE_WHAT)
    }
}

val LEAVES = intArrayOf(
    R.drawable.leaf_1,
    R.drawable.leaf_2,
    R.drawable.leaf_3,
    R.drawable.leaf_4,
    R.drawable.leaf_5,
    R.drawable.leaf_6,
    R.drawable.leaf_7,
)

private val mAllImageViews = ArrayList<View>()

class MHandler(val context : Context, val mDisplaySize: Rect, val mScale : Float, val mRootLayout: ViewGroup): Handler(Looper.getMainLooper()){
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val viewId = Random().nextInt(LEAVES.size)
        val d = context.resources.getDrawable(LEAVES[viewId])
        val inflate = LayoutInflater.from(context)
        val imageView: ImageView = inflate.inflate(R.layout.ani_image_view, null) as ImageView
        imageView.setImageDrawable(d)
        mRootLayout.addView(imageView)
        mAllImageViews.add(imageView)
        val animationLayout: RelativeLayout.LayoutParams = imageView.getLayoutParams() as RelativeLayout.LayoutParams
        animationLayout.setMargins(0, (-150 * mScale).toInt(), 0, 0)
        animationLayout.width = (30 * mScale).toInt()
        animationLayout.height = (30 * mScale).toInt()
        startLeavesAnimation(imageView, mDisplaySize, mScale)
    }
}

fun startLeavesAnimation(aniView: ImageView, mDisplaySize: Rect, mScale : Float) {
    aniView.pivotX = (aniView.width / 2).toFloat()
    aniView.pivotY = (aniView.height / 2).toFloat()
    val delay = Random().nextInt(MenuFragment.MAX_DELAY).toLong()
    val animator = ValueAnimator.ofFloat(0f, 1f)
    animator.duration = MenuFragment.ANIM_DURATION
    animator.interpolator = AccelerateInterpolator()
    animator.startDelay = delay
    animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
        var angle = 50 + (Math.random() * 101).toInt()
        var movex =   (0..mDisplaySize.right).random()
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val value = (animation.animatedValue as Float).toFloat()
            aniView.rotation = angle * value
            aniView.translationX =  movex.toFloat()   // (movex - 40) * value
            aniView.translationY = (mDisplaySize.bottom + 150 * mScale) * value
        }
    })
    animator.start()
}