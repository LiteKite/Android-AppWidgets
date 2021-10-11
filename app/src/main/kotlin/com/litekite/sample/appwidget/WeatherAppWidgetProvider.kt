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
package com.litekite.sample.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import android.widget.RemoteViews
import com.litekite.sample.R
import com.litekite.sample.main.MainActivity

class WeatherAppWidgetProvider : AppWidgetProvider() {

    companion object {
        private val TAG = WeatherAppWidgetProvider::class.java.simpleName
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
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Tell the AppWidgetManager to perform an update on the current app widget
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val smallView = RemoteViews(
                    context.packageName,
                    R.layout.layout_app_widget_weather_small
                ).apply {
                    setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
                }

                val mediumView = RemoteViews(
                    context.packageName,
                    R.layout.layout_app_widget_weather_medium
                ).apply {
                    setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
                }

                val largeView = RemoteViews(
                    context.packageName,
                    R.layout.layout_app_widget_weather_large
                ).apply {
                    setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
                }

                val viewMapping: Map<SizeF, RemoteViews> = mapOf(
                    SizeF(180f, 120f) to smallView,
                    SizeF(270f, 120f) to mediumView,
                    SizeF(270f, 270f) to largeView
                )

                appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(viewMapping))
            } else {
                val views: RemoteViews = RemoteViews(
                    context.packageName,
                    R.layout.layout_app_widget_weather_small
                ).apply {
                    setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return
        }

        val widgetOptions: Bundle = appWidgetManager.getAppWidgetOptions(appWidgetId)

        val minWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val maxWidth = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
        val maxHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

        Log.d(TAG, "onAppWidgetOptionsChanged: minWidth: $minWidth")
        Log.d(TAG, "onAppWidgetOptionsChanged: minHeight: $minHeight")
        Log.d(TAG, "onAppWidgetOptionsChanged: maxWidth: $maxWidth")
        Log.d(TAG, "onAppWidgetOptionsChanged: maxHeight: $maxHeight")

        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.layout_app_widget_weather_small
        ).apply {
            setOnClickPendingIntent(R.id.widget_weather, getWidgetPendingIntent(context))
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
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

    // Create the RemoteViews for the given size.
    private fun createRemoteViews(context: Context, size: SizeF): RemoteViews {
        Log.d(TAG, "createRemoteViews: size: $size")
        return RemoteViews(context.packageName, R.layout.layout_app_widget_weather_small)
    }

    /**
     * Called every time the widget is deleted from the AppWidgetHost
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
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
        super.onEnabled(context)
    }

    /**
     * Called when the widget instance deleted from the AppWidgetHost.
     *
     * Cleanup everything you created on [onEnabled]
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

    /**
     * YOU DON'T NEED TO IMPLEMENT THIS
     * AS THE [AppWidgetProvider] DISPATCHES THROUGH ABOVE CALLBACKS.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }
}
