package site.kotty_kov.Amusement_vo1.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel(){

        private var musMode = true
        private val _mutableSelectedItem = MutableLiveData<Boolean>(musMode)
        val music: LiveData<Boolean> get() = _mutableSelectedItem

        fun switchONOff() {
            musMode = !musMode
            _mutableSelectedItem.value = musMode
        }

    fun pause() {
        musMode = false
    }

}