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
package com.litekite.sample.appwidget.weather

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WeatherAppWidgetWorkScheduler {

    fun scheduleOneShotWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString(
                WeatherAppWidgetWorker.WEATHER_APP_WIDGET_WORK_TYPE,
                WeatherAppWidgetWorker.ONE_TIME_WORK
            ).build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherAppWidgetWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WeatherAppWidgetWorker.ONE_TIME_WORK,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun schedulePeriodicWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString(
                WeatherAppWidgetWorker.WEATHER_APP_WIDGET_WORK_TYPE,
                WeatherAppWidgetWorker.PERIODIC_WORK
            ).build()

        val workRequest = PeriodicWorkRequestBuilder<WeatherAppWidgetWorker>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS
        ).addTag(WeatherAppWidgetWorker.PERIODIC_WORK)
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeatherAppWidgetWorker.PERIODIC_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
