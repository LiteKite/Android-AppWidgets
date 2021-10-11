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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import com.litekite.sample.R

class CollectionWidgetProvider : AppWidgetProvider() {

    companion object {
        private val TAG = CollectionWidgetProvider::class.java.simpleName
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget
    // displays a Toast message for the current item.
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TOAST_ACTION) {
            val appWidgetId: Int = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            Log.d(TAG, "onReceive: appWidgetId: $appWidgetId")
            val viewIndex: Int = intent.getIntExtra(EXTRA_ITEM, 0)
            Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // update each of the app widgets with the remote adapter
        appWidgetIds.forEach { appWidgetId ->
            // Set up the intent that starts the StackViewService, which will
            // provide the views for this collection.
            val intent = Intent(context, RemoteViewService::class.java).apply {
                // Add the app widget ID to the intent extras.
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            // Instantiate the RemoteViews object for the app widget layout.
            val rv = RemoteViews(context.packageName, R.layout.layout_collection_app_widget).apply {
                // Set up the RemoteViews object to use a RemoteViews adapter.
                // This adapter connects
                // to a RemoteViewsService  through the specified intent.
                // This is how you populate the data.
                setRemoteAdapter(R.id.stack_view, intent)

                // The empty view is displayed when the collection has no items.
                // It should be in the same layout used to instantiate the RemoteViews
                // object above.
                setEmptyView(R.id.stack_view, R.id.empty_view)
            }

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a
            // collection cannot set up their own pending intents. Instead, the collection as a
            // whole sets up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            val toastPendingIntent: PendingIntent = Intent(
                context,
                CollectionWidgetProvider::class.java
            ).run {
                // Set the action for the intent.
                // When the user touches a particular view, it will have the effect of
                // broadcasting TOAST_ACTION.
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))

                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }

                PendingIntent.getBroadcast(
                    context,
                    0,
                    this,
                    flags
                )
            }
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            // appWidgetManager.notifyAppWidgetViewDataChanged()

            // Do additional processing specific to this app widget...
            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
    }
}

class RemoteViewService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

private const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
private const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"
private const val REMOTE_VIEW_COUNT: Int = 10

class StackRemoteViewsFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    companion object {
        private val TAG = StackRemoteViewsFactory::class.java.simpleName
    }

    private lateinit var collectionWidgetItems: MutableList<CollectionWidgetItem>
    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        collectionWidgetItems = MutableList(REMOTE_VIEW_COUNT) { index ->
            CollectionWidgetItem("$index!")
        }
        Log.d(TAG, "onCreate: appWidgetId: $appWidgetId")
    }

    /**
     * Can do intense work
     */
    override fun onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
    }

    override fun onDestroy() {
        collectionWidgetItems.clear()
    }

    override fun getCount(): Int = collectionWidgetItems.size

    /**
     * Can do intense work
     */
    override fun getViewAt(position: Int): RemoteViews {
        // Construct a remote views item based on the app widget item XML file,
        // and set the text based on the position.
        return RemoteViews(context.packageName, R.layout.adapter_collection_widget_item).apply {
            setTextViewText(R.id.widget_item, collectionWidgetItems[position].text)

            // Next, set a fill-intent, which will be used to fill in the pending intent template
            // that is set on the collection view in StackWidgetProvider.
            val fillInIntent = Intent().apply {
                Bundle().also { extras ->
                    extras.putInt(EXTRA_ITEM, position)
                    putExtras(extras)
                }
            }
            // Make it possible to distinguish the individual on-click
            // action of a given item
            setOnClickFillInIntent(R.id.widget_item, fillInIntent)
        }
    }

    override fun getLoadingView(): RemoteViews? {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null
    }

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
