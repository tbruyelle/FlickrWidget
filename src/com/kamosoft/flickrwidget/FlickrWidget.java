/**
 * Copyright - Accor - All Rights Reserved www.accorhotels.com
 */
package com.kamosoft.flickrwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author <a href="mailto:thomas.bruyelle@accor.com">tbruyelle</a>
 * created 10 mars 2011
 * @since 
 * @version $Id$
 */
public class FlickrWidget
    extends AppWidgetProvider
{
    /**
     * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])
     */
    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
    {
        context.startService( new Intent( context, UpdateService.class ) );
    }

    public static class UpdateService
        extends Service
    {
        /**
         * @see android.app.Service#onStart(android.content.Intent, int)
         */
        @Override
        public void onStart( Intent intent, int startId )
        {
            super.onStart( intent, startId );
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
}
