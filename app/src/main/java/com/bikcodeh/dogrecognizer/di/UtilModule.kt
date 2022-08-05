package com.bikcodeh.dogrecognizer.di

import android.content.Context
import com.bikcodeh.dogrecognizer.ml.Classifier
import com.bikcodeh.dogrecognizer.presentation.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import org.tensorflow.lite.support.common.FileUtil
import javax.inject.Singleton

@Module
@InstallIn(FragmentComponent::class)
object UtilModule {

    @Provides
    @FragmentScoped
    fun providesClassifier(@ApplicationContext context: Context): Classifier {
        return Classifier(
            FileUtil.loadMappedFile(context, Constants.MODEL_PATH),
            FileUtil.loadLabels(context, Constants.LABEL_PATH)
        )
    }
}