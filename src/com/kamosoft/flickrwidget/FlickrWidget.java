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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;

/**
 * @author Tom
 * created 10 mars 2011
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
        Log.d( "FlickrWidget: Start onUpdate with " + appWidgetIds.length + " widgets" );

        int[] appWidgetIds2 = appWidgetManager.getAppWidgetIds( new ComponentName( context, FlickrWidget.class ) );
        Log.d( "FlickrWidget: installed : " + appWidgetIds2.length + " widgets" );
        for ( int i = 0; i < appWidgetIds2.length; i++ )
        {
            Log.d( "FlickrWidget : installed appWidgetId = " + appWidgetIds2[i] );
        }

        for ( int i = 0; i < appWidgetIds.length; i++ )
        {
            Log.d( "FlickrWidget : appWidgetId = " + appWidgetIds[i] );

            /* we check if the the widget is well configured by calling WidgetConfifuration.isDisplayable 
             * this prevent from start the WidgetUpdateService while the FlickrWidgetConfigure is not finish */
            WidgetConfiguration widgetConfiguration = FlickrWidgetConfigure
                .loadConfiguration( context, appWidgetIds[i] );
            if ( widgetConfiguration.isDisplayable() )
            {
                /* if displayable, we start a service to update the current widget */
                Log.d( "appWidgetId " + appWidgetIds[i] + " is displayable" );
                WidgetUpdateService.start( context, appWidgetIds[i] );
            }
            else
            {
                //do nothing
                Log.d( "appWidgetId " + appWidgetIds[i] + " is NOT displayable" );
            }
        }

        Log.d( "FlickrWidget: end onUpdate" );
    }

    /**
     * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
     */
    @Override
    public void onDeleted( Context context, int[] appWidgetIds )
    {
        super.onDeleted( context, appWidgetIds );
        Log.d( "FlickrWidget: Start onDeleted with " + appWidgetIds.length + " widgets" );
        for ( int i = 0; i < appWidgetIds.length; i++ )
        {
            Log.d( "FlickrWidget : removing configuration for appWidgetId = " + appWidgetIds[i] );

            /* we clean the widget configuration */
            FlickrWidgetConfigure.clearConfiguration( context, appWidgetIds[i] );
        }
        Log.d( "FlickrWidget: end onDelete" );

    }

}
