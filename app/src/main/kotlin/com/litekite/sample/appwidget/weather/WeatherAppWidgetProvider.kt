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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import android.widget.RemoteViews
import androidx.work.WorkManager
import com.litekite.sample.R
import com.litekite.sample.main.MainActivity

class WeatherAppWidgetProvider : AppWidgetProvider() {

    companion object {
        private val TAG = WeatherAppWidgetProvider::class.java.simpleName

        fun getProvider(context: Context) = ComponentName(
            context,
            WeatherAppWidgetProvider::class.java
        )

        fun updateWeatherAppWidget(
            context: Context,
            appWidgetId: Int,
            appWidgetManager: AppWidgetManager
        ) {
            val widgetOptions: Bundle = appWidgetManager.getAppWidgetOptions(appWidgetId)

            val minWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            val minHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

            createRemoteViews(
                context,
                SizeF(minWidth.toFloat(), minHeight.toFloat())
            )?.let {
                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, it)
            }
        }

        /**
         * Creates the RemoteViews for the given size.
         *
         * Specify the minimum width and height in dp and a layout,
         * which you want to use for the specified size
         *
         * In the following case:
         *
         * - Small is used from
         * 180dp (or minResizeWidth) x 120dp (or minResizeHeight)
         * to 269dp (next cutoff point - 1) x 269dp (next cutoff point - 1)
         *
         * - Medium is used from
         * 270dp x 110dp to 270dp x 279dp (next cutoff point - 1)
         *
         * - Large is used from
         * 270dp x 280dp to 570dp (specified as maxResizeWidth) x 450dp (specified as maxResizeHeight)
         */
        private fun createRemoteViews(context: Context, size: SizeF): RemoteViews? {
            Log.d(TAG, "createRemoteViews: size: $size")
            if (size.width in 180f..269f && size.height in 120f..269f) {
                return createSmallWeatherWidget(context)
            } else if (size.width in 270f..329f && size.height in 120f..180f) {
                return createMediumWeatherWidget(context)
            } else if (size.width in 270f..570f && size.height in 120f..450f) {
                return createLargeWeatherWidget(context)
            }
            return null
        }

        private fun createSmallWeatherWidget(context: Context): RemoteViews {
            return RemoteViews(
                context.packageName,
                R.layout.layout_app_widget_weather_small
            ).apply {
                setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
            }
        }

        private fun createMediumWeatherWidget(context: Context): RemoteViews {
            return RemoteViews(
                context.packageName,
                R.layout.layout_app_widget_weather_medium
            ).apply {
                setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
            }
        }

        private fun createLargeWeatherWidget(context: Context): RemoteViews {
            return RemoteViews(
                context.packageName,
                R.layout.layout_app_widget_weather_large
            ).apply {
                setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
            }
        }

        private fun getWidgetPendingIntent(context: Context): PendingIntent {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            // Create an Intent to launch MainActivity
            return PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                flags
            )
        }
    }

    /**
     * Called when the updatePeriodMillis in AppWidgetProviderInfo defined in app_widget_info.xml
     * interval expires and it's time to update the widget.
     *
     * Also called upon when the user adds the widget to the home screen or launcher.
     *
     * It will not be called when the configuration activity defined and
     * called for subsequent interval calls.
     *
     * Register touch event callbacks here only that does not require
     * database or other intense work or start a service or background thread
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate:")
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            updateWeatherAppWidget(context, appWidgetId, appWidgetManager)
        }
    }

    /**
     * Called when the widget placed on the launcher or home screen.
     *
     * Called when user resizes the widget.
     * Show/hide/manage content based on the size ranges
     * from [AppWidgetManager.getAppWidgetOptions] that returns a [Bundle]
     *
     * Available dp size units:
     *
     * [AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH]
     * [AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT]
     * [AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH]
     * [AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT]
     */
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        Log.d(TAG, "onAppWidgetOptionsChanged:")
        updateWeatherAppWidget(context, appWidgetId, appWidgetManager)
    }

    /**
     * Called every time the widget is deleted from the AppWidgetHost
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d(TAG, "onDeleted:")
        super.onDeleted(context, appWidgetIds)
    }

    /**
     * Called when the widget instance created only for the first time.
     *
     * It won't be called when you try to add second widget of the same widget type.
     *
     * Perform any setup like database instance creation, start remote call, etc.
     */
    override fun onEnabled(context: Context) {
        Log.d(TAG, "onEnabled:")
        WeatherAppWidgetWorkScheduler.scheduleOneShotWork(context)
        WeatherAppWidgetWorkScheduler.schedulePeriodicWork(context)
    }

    /**
     * Called when the widget instance deleted from the AppWidgetHost.
     *
     * Cleanup everything you created on [onEnabled]
     */
    override fun onDisabled(context: Context) {
        Log.d(TAG, "onDisabled:")
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(WeatherAppWidgetWorker.PERIODIC_WORK)
        workManager.cancelAllWorkByTag(WeatherAppWidgetWorker.ONE_TIME_WORK)
    }

    /**
     * YOU DON'T NEED TO IMPLEMENT THIS
     * AS THE [AppWidgetProvider] DISPATCHES THROUGH ABOVE CALLBACKS.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive:")
        super.onReceive(context, intent)
    }
}
