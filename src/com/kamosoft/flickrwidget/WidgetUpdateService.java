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

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.Html;
import android.widget.RemoteViews;

import com.kamosoft.flickr.FlickrConnect;
import com.kamosoft.flickr.model.Event;
import com.kamosoft.flickr.model.FlickrApiResult;
import com.kamosoft.flickr.model.Item;
import com.kamosoft.flickr.model.Photo;

/**
 * @author Tom
 * created 16 mars 2011
 */
public class WidgetUpdateService
    extends Service
{
    /**
     * used to downloads the image that may be included in some comments
     */
    private Html.ImageGetter mHtmlImageGetter;

    private FlickrConnect mFlickrConnect;

    public static void start( Context context, int appWidgetId )
    {
        Intent intent = new Intent( context, WidgetUpdateService.class );
        intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId );
        context.startService( intent );
    }

    /**
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d( "WidgetUpdateService: onDestroy" );
    }

    /**
     * @see android.app.Service#onLowMemory()
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Log.d( "WidgetUpdateService: onLowMemory" );
    }

    /**
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart( Intent intent, int startId )
    {
        super.onStart( intent, startId );

        Log.d( "WidgetUpdateService: Start onStart" );
        mFlickrConnect = new FlickrConnect( this );
        if ( !mFlickrConnect.IsLoggedIn() )
        {
            Log.e( "WidgetUpdateService: not authenticated" );
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

        /* start to update the widget with an asynctask */
        new WidgetUpdateTask( this, FlickrWidgetConfigure.loadConfiguration( this, appWidgetId ), appWidgetId )
            .execute();

        /* stop the service */
        this.stopSelf();
        Log.d( "WidgetUpdateService: end onStart" );
    }

    private class WidgetUpdateTask
        extends AsyncTask<Void, Void, Boolean>
    {
        private Context mContext;

        private WidgetConfiguration mWidgetConfiguration;

        private int mAppWidgetId;

        private RemoteViews mRootViews;

        public WidgetUpdateTask( Context context, WidgetConfiguration widgetConfiguration, int appWidgetId )
        {
            mContext = context;
            mWidgetConfiguration = widgetConfiguration;
            mAppWidgetId = appWidgetId;
            // Get the layout for the App Widget and attach an on-click listener to the button
            mRootViews = new RemoteViews( mContext.getPackageName(), R.layout.appwidget );
        }

        /**
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Boolean doInBackground( Void... params )
        {
            Log.d( "WidgetUpdateTask: start updateWidget" );

            if ( !mWidgetConfiguration.isDisplayable() )
            {
                Log.i( "WidgetUpdateTask: nothing to display" );
                return true;
            }

            mRootViews.removeAllViews( R.id.root );

            // Create an Intent to launch the Flickr website in the default browser
            Intent widgetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( Constants.FLICKR_ACTIVITY_URL ) );
            PendingIntent pendingIntent = PendingIntent.getActivity( mContext, 0, widgetIntent, 0 );
            // attach an on-click listener          
            mRootViews.setOnClickPendingIntent( R.id.root, pendingIntent );

            String userId = mFlickrConnect.getFlickrParameters().getNsid();
            if ( userId == null )
            {
                Log.e( "WidgetUpdateTask: userId is null" );
                return false;
            }

            FlickrApiResult flickrApiResult = mFlickrConnect.getActivityUserPhotos( userId, Constants.TIME_FRAME,
                                                                                    String
                                                                                        .valueOf( mWidgetConfiguration
                                                                                            .getMaxItems() ), "1" );
            if ( flickrApiResult == null )
            {
                Log.e( "WidgetUpdateTask: jsApi is null" );
                return false;
            }
            Log.i( "WidgetUpdateTask: JsonFlickrApi retrieved with " + flickrApiResult.getItems().getItems().size()
                + " items" );

            if ( flickrApiResult.getItems().getItems().isEmpty() )
            {
                mRootViews.addView( R.id.root, new RemoteViews( mContext.getPackageName(), R.layout.nothing ) );
                return true;
            }
            
            for ( Item item : flickrApiResult.getItems().getItems() )
            {
                RemoteViews itemRemoteViews = null;
                Log.d( "WidgetUpdateTask: processing item " + item.getType() );
                switch ( item.getType() )
                {
                    case photo:
                        itemRemoteViews = new RemoteViews( mContext.getPackageName(), R.layout.item_photo );

                        Photo photo = mFlickrConnect.getPhotoInfo( item.getId() ).getPhoto();
                        try
                        {
                            itemRemoteViews.setImageViewBitmap( R.id.photoBitmap,
                                                                photo.getBitmap( Photo.Size.SMALLSQUARE ) );
                            itemRemoteViews.setTextViewText( R.id.photoText, item.getTitle().getContent() );
                        }
                        catch ( Exception e )
                        {
                            Log.e( e.getMessage(), e );
                            itemRemoteViews.setTextViewText( R.id.photoText, e.getMessage() );
                        }

                        for ( Event event : item.getActivity().getEvents() )
                        {
                            RemoteViews eventRemoteViews = new RemoteViews( mContext.getPackageName(),
                                                                            R.layout.event_photo );
                            Log.d( "WidgetUpdateTask: processing event " + event.getType() );
                            switch ( event.getType() )
                            {
                                case added_to_gallery:
                                    eventRemoteViews.setImageViewResource( R.id.event_photo_icon, R.drawable.expo );

                                    eventRemoteViews.setTextViewText( R.id.event_photo_text, Html
                                        .fromHtml( getString( R.string.added_to_gallery, event.getUsername() ),
                                                   getHtmlImageGetter(), null ) );
                                    break;

                                case comment:
                                    eventRemoteViews.setImageViewResource( R.id.event_photo_icon, R.drawable.comment );
                                    eventRemoteViews.setTextViewText( R.id.event_photo_text,
                                                                      Html.fromHtml( getString( R.string.comment,
                                                                                                event.getUsername(),
                                                                                                event.getContent() ),
                                                                                     getHtmlImageGetter(), null ) );
                                    break;

                                case fave:
                                    eventRemoteViews.setImageViewResource( R.id.event_photo_icon, R.drawable.fave );
                                    eventRemoteViews.setTextViewText( R.id.event_photo_text, Html
                                        .fromHtml( getString( R.string.fave, event.getUsername() ),
                                                   getHtmlImageGetter(), null ) );
                                    break;

                                default:
                                    Log.e( "WidgetUpdateTask: unhandled event Type : " + event.getType() );
                                    eventRemoteViews.setTextViewText( R.id.event_photo_text, "unhandled event Type : "
                                        + event.getType() );
                            }
                            itemRemoteViews.addView( R.id.events, eventRemoteViews );
                        }
                        break;

                    default:
                        Log.e( "WidgetUpdateTask: unhandled Item Type : " + item.getType() );
                }
                if ( itemRemoteViews != null )
                {
                    mRootViews.addView( R.id.root, itemRemoteViews );
                }
            }

            Log.d( "WidgetUpdateTask : end updateWidget" );
            return true;
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute( Boolean result )
        {
            if ( !result.booleanValue() )
            {
                // display an error message if it goes wrong
                mRootViews.removeAllViews( R.id.root );
                mRootViews.addView( R.id.root, new RemoteViews( mContext.getPackageName(), R.layout.error ) );
            }
            // Push update for this widget to the home screen
            AppWidgetManager manager = AppWidgetManager.getInstance( mContext );
            manager.updateAppWidget( mAppWidgetId, mRootViews );
        }

        private Html.ImageGetter getHtmlImageGetter()
        {
            if ( mHtmlImageGetter == null )
            {
                mHtmlImageGetter = new Html.ImageGetter()
                {
                    private Drawable mBlank;

                    @Override
                    public Drawable getDrawable( String source )
                    {
                        // we don't display the images in the comments
                        if ( mBlank == null )
                        {
                            mBlank = getResources().getDrawable( R.drawable.blank );
                            mBlank.setBounds( 0, 0, 1, 1 );
                        }
                        return mBlank;
                    }
                };
            }
            return mHtmlImageGetter;
        }

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