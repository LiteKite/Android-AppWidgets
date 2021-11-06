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

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class WeatherAppWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private val TAG = WeatherAppWidgetWorker::class.java.simpleName
        const val WEATHER_APP_WIDGET_WORK_TYPE = "weather_app_widget_work_type"
        const val PERIODIC_WORK = "news_app_widget_periodic_work"
        const val ONE_TIME_WORK = "news_app_widget_one_time_work"
    }

    private val appWidgetManager = AppWidgetManager.getInstance(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "doWork:")
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            WeatherAppWidgetProvider.getProvider(context)
        )
        appWidgetIds.forEach { appWidgetId ->
            WeatherAppWidgetProvider.updateWeatherAppWidget(
                context,
                appWidgetId,
                appWidgetManager
            )
        }
        return@withContext Result.success()
    }
}
