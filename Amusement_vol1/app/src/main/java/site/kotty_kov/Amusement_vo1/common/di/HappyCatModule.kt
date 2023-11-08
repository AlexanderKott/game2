package site.kotty_kov.Amusement_vo1.common.di

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import site.kotty_kov.Amusement_vo1.common.*
import site.kotty_kov.Amusement_vo1.funnymath.FunnyMathGen
import site.kotty_kov.Amusement_vo1.funnymath.FunnyMathLogic
import site.kotty_kov.Amusement_vo1.funnymath.PrepareTile
import site.kotty_kov.Amusement_vo1.funnymath.ScreenConfigMath
import site.kotty_kov.Amusement_vo1.happycat.HappyCatGen
import site.kotty_kov.Amusement_vo1.happycat.HappyCatLogic
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HappyCatGame


@Module
@InstallIn(FragmentComponent::class)
object HappyCatModule {



    @FragmentScoped
    @HappyCatGame
    @Provides
    fun provideGSound(@ApplicationContext appContext: Context): GSound {
        return GSound(appContext)
    }


    @FragmentScoped
    @HappyCatGame
    @Provides
    fun provideGenerator(): HappyCatGen {
        return HappyCatGen()
    }



    @FragmentScoped
    @HappyCatGame
    @Provides
    fun provideModel(@ApplicationContext appContext: Context , @HappyCatGame generator : HappyCatGen): HappyCatLogic {
        return HappyCatLogic(
            Storage(CommonConstants.CATS_GAME, appContext), generator
        )
    }



    @FragmentScoped
    @HappyCatGame
    @Provides
    fun provideScreenConfigMath(@ApplicationContext appContext: Context): ScreenConfig {
        return ScreenConfig(
            appContext.resources.displayMetrics.widthPixels,
            22,
            8
        )
    }

}