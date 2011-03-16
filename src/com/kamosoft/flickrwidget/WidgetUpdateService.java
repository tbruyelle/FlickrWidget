/**
 * Copyright 2011 kamosoft
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kamosoft.flickrwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.kamosoft.flickr.APICalls;
import com.kamosoft.flickr.GlobalResources;
import com.kamosoft.flickr.RestClient;
import com.kamosoft.flickr.GlobalResources.ImgSize;
import com.kamosoft.flickr.model.Event;
import com.kamosoft.flickr.model.Item;
import com.kamosoft.flickr.model.JsonFlickrApi;
import com.kamosoft.flickr.model.Photo;

/**
 * @author Tom
 * created 16 mars 2011
 */
public class WidgetUpdateService
    extends Service
{
    public static void updateWidget( Context context, WidgetConfiguration widgetConfiguration, int appWidgetId )
    {
        Log.d( "WidgetUpdateService : start updateWidget" );
        // Get the layout for the App Widget and attach an on-click listener to the button
        RemoteViews rootViews = new RemoteViews( context.getPackageName(), R.layout.appwidget );

        SharedPreferences flickrLibraryPrefs = context.getSharedPreferences( GlobalResources.PREFERENCES_ID, 0 );
        String userId = flickrLibraryPrefs.getString( GlobalResources.PREF_USERID, null );
        if ( userId == null )
        {
            Log.e( "userId is null" );
            return;
        }

        JsonFlickrApi jsApi = APICalls.getActivityUserPhotos( userId, "15d", "", "" );
        if ( jsApi == null )
        {
            Log.e( "jsApi is null" );
            return;
        }
        Log.i( "JsonFlickrApi retrieved with " + jsApi.getItems().getItems().size() + " items" );

        for ( Item item : jsApi.getItems().getItems() )
        {
            RemoteViews itemRemoteViews = null;
            Log.d( "processing item " + item.getType() );
            switch ( item.getType() )
            {
                case photo:
                    itemRemoteViews = new RemoteViews( context.getPackageName(), R.layout.item_photo );

                    Photo photo = APICalls.getPhotoInfo( item.getId() ).getPhoto();
                    try
                    {
                        itemRemoteViews.setImageViewBitmap( R.id.photoBitmap, GlobalResources
                            .getBitmapFromURL( photo, ImgSize.SMALLSQUARE ) );
                        itemRemoteViews.setTextViewText( R.id.photoText, item.getTitle().getContent() );
                    }
                    catch ( Exception e )
                    {
                        Log.e( e.getMessage(), e );
                        itemRemoteViews.setTextViewText( R.id.photoText, e.getMessage() );
                    }

                    for ( Event event : item.getActivity().getEvents() )
                    {
                        RemoteViews eventRemoveViews = new RemoteViews( context.getPackageName(), R.layout.event_photo );
                        Log.d( "processing event " + event.getType() );
                        switch ( event.getType() )
                        {
                            case added_to_gallery:
                                eventRemoveViews.setTextViewText( R.id.event_photo_text,
                                                                  "added to gallery by " + event.getUsername() );
                                break;

                            case comment:
                                eventRemoveViews.setTextViewText( R.id.event_photo_text, event.getContent() );
                                break;

                            case fave:
                                eventRemoveViews.setTextViewText( R.id.event_photo_text,
                                                                  "added to " + event.getUsername() + "'s favorites" );
                                break;

                            default:
                                Log.e( "unhandled event Type : " + event.getType() );
                                eventRemoveViews.setTextViewText( R.id.event_photo_text, "unhandled event Type : "
                                    + event.getType() );
                        }
                        itemRemoteViews.addView( R.id.events, eventRemoveViews );
                    }
                    break;

                default:
                    Log.e( "unhandled Item Type : " + item.getType() );
            }
            if ( itemRemoteViews != null )
            {
                Log.d( "Adding the item view" );
                rootViews.addView( R.id.root, itemRemoteViews );
            }

        }

        // Push update for this widget to the home screen
        AppWidgetManager manager = AppWidgetManager.getInstance( context );
        manager.updateAppWidget( appWidgetId, rootViews );
        Log.d( "WidgetUpdateService : end updateWidget" );
    }

    /**
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart( Intent intent, int startId )
    {
        super.onStart( intent, startId );

        Log.d( "WidgetUpdateService: Start onStart" );
        RestClient.setAuth( this );
        if ( !APICalls.authCheckToken() )
        {
            Log.i( "WidgetUpdateService: not authenticated" );
            stopSelf();
            return;
        }
        int appWidgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID,
                                              AppWidgetManager.INVALID_APPWIDGET_ID );
        if ( appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID )
        {
            Log.e( "Error bad appWidgetId" );
            stopSelf();
            return;
        }
        WidgetConfiguration widgetConfiguration = FlickrWidgetConfigure.loadConfiguration( this, appWidgetId );

        updateWidget( this, widgetConfiguration, appWidgetId );

        /* stop the service */
        this.stopSelf();
        Log.d( "WidgetUpdateService: end onStart" );
    }

    /**
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind( Intent arg0 )
    {
        return null;
    }
}