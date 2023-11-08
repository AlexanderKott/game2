package site.kotty_kov.Amusement_vo1.common.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import site.kotty_kov.Amusement_vo1.common.CommonConstants
import site.kotty_kov.Amusement_vo1.common.GSound
import site.kotty_kov.Amusement_vo1.common.ScreenConfig
import site.kotty_kov.Amusement_vo1.common.Storage
import site.kotty_kov.Amusement_vo1.music.MusicGen
import site.kotty_kov.Amusement_vo1.music.MusicLogic
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MusicGame


@Module
@InstallIn(FragmentComponent::class)
object MusicGameModule {

    @FragmentScoped
    @MusicGame
    @Provides
    fun provideGSound(@ApplicationContext appContext: Context): GSound {
        return GSound(appContext)
    }


    @FragmentScoped
    @MusicGame
    @Provides
    fun provideGenerator(): MusicGen {
        return MusicGen()
    }


    @FragmentScoped
    @MusicGame
    @Provides
    fun provideModel(@ApplicationContext appContext: Context , @MusicGame generator : MusicGen): MusicLogic {
        return MusicLogic(Storage(CommonConstants.MUSIC_GAME, appContext), generator)
    }


    @FragmentScoped
    @MusicGame
    @Provides
    fun provideScreenConfig(@ApplicationContext appContext: Context): ScreenConfig {
        return  ScreenConfig(
            appContext.resources.displayMetrics.widthPixels,
            35,
            11
        )
    }

}