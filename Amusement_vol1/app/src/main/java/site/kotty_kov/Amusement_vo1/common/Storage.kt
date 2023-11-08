package site.kotty_kov.Amusement_vo1.common

import android.content.Context
import android.content.SharedPreferences

class Storage(private var nameOfSharedPrefs : String, private val context: Context) {

   private companion object {
        val REC_SPORT = "record_sport"
        val REC_NORMAL = "record_normal"
    }

   private lateinit var sharedPref : SharedPreferences

    fun setScoreMultiplier(multiPlierName: String){
         nameOfSharedPrefs += multiPlierName
         sharedPref = context.getSharedPreferences(nameOfSharedPrefs, Context.MODE_PRIVATE)
    }

   fun getPreviousSportRecord() : Int {
       return sharedPref.getInt(REC_SPORT, 0)
   }
    fun getPreviousNormalRecord(): Int {
        return sharedPref.getInt(REC_NORMAL, 0)
    }

    fun saveGameResult(isSport: Boolean, scores : Int){
        with(sharedPref.edit()) {
            if (isSport) {
                putInt(REC_SPORT, scores)
            } else {
                putInt(REC_NORMAL, scores)
            }
            apply()
        }
    }
}