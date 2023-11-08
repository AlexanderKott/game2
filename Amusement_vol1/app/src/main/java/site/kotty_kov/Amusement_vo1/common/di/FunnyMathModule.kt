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
import site.kotty_kov.Amusement_vo1.common.CommonConstants
import site.kotty_kov.Amusement_vo1.common.GSound
import site.kotty_kov.Amusement_vo1.common.Storage
import site.kotty_kov.Amusement_vo1.common.setUpAlertDialog
import site.kotty_kov.Amusement_vo1.funnymath.FunnyMathGen
import site.kotty_kov.Amusement_vo1.funnymath.FunnyMathLogic
import site.kotty_kov.Amusement_vo1.funnymath.PrepareTile
import site.kotty_kov.Amusement_vo1.funnymath.ScreenConfigMath
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FunnyMathGame


@Module
@InstallIn(FragmentComponent::class)
object FunnyMathModule {


    @FragmentScoped
    @FunnyMathGame
    @Provides
    fun provideGSound(@ApplicationContext appContext: Context): GSound {
        return GSound(appContext)
    }


    @FragmentScoped
    @FunnyMathGame
    @Provides
    fun provideGenerator(): FunnyMathGen {
        return FunnyMathGen()
    }


    @FragmentScoped
    @FunnyMathGame
    @Provides
    fun provideModel(@ApplicationContext appContext: Context , @FunnyMathGame generator : FunnyMathGen): FunnyMathLogic {
        return FunnyMathLogic(
            Storage(CommonConstants.MATH_GAME, appContext), generator
        )
    }


    @FragmentScoped
    @FunnyMathGame
    @Provides
    fun provideTile(): GradientDrawable {
        return PrepareTile.prepare()
    }


    @FragmentScoped
    @FunnyMathGame
    @Provides
    fun provideScreenConfigMath(@ApplicationContext appContext: Context): ScreenConfigMath {
        return ScreenConfigMath(
            scrWidth = appContext.resources.displayMetrics.widthPixels,
            answerButtonsSize = 14,
            answerButtonsLeftPadding = 5,
            answerButtonsTopPadding = 60,
            answerButtonsX = 5,
            answerButtonsY = 5,

            panelSize = 20,
            panelLeftPadding = 0,
            panelTopPadding = 0,
            panelX = 0,
            panelY = 36,

            smallFontSize = 4,
            bigFontSize = 6
        )
    }

}