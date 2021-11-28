/*
 * Copyright 2021 LiteKite Startup. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.litekite.sample.app

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.work.Configuration as WorkManagerConfig

/**
 * Application class.
 *
 * @author Vignesh S
 * @version 1.0, 07/11/2021
 * @since 1.0
 */
@HiltAndroidApp
class WidgetsApp : Application(), WorkManagerConfig.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object {
        val TAG: String = WidgetsApp::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate:")
    }

    override fun getWorkManagerConfiguration(): WorkManagerConfig {
        return WorkManagerConfig.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged:")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "onTerminate:")
    }
}
