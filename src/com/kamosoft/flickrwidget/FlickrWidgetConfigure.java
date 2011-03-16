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

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.kamosoft.flickr.APICalls;
import com.kamosoft.flickr.AuthenticateActivity;
import com.kamosoft.flickr.GlobalResources;
import com.kamosoft.flickr.RestClient;

/**
 * The widget configuration activity.
 * Perform the flickr authentification and other stuff
 * @author Tom
 * created 10 mars 2011
 */
public class FlickrWidgetConfigure
    extends Activity
    implements View.OnClickListener
{
    private int mAppWidgetId;

    private static final int AUTHENTICATE = 0;

    private CheckBox mCheckBoxUserPhotos;

    private CheckBox mCheckBoxUserComments;

    private Button mCommitButton;

    private SharedPreferences mFlickrLibraryPrefs;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Log.d( "FlickrWidgetConfigure: Start onCreate" );
        /* set the Activity result to RESULT_CANCELED. 
         * This way, if the user backs-out of the Activity before reaching the end, 
         * the App Widget host is notified that the configuration was cancelled and the App Widget will not be added. */
        setResult( RESULT_CANCELED );

        /* retrieve the widget id */
        mAppWidgetId = getIntent().getExtras().getInt( AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                       AppWidgetManager.INVALID_APPWIDGET_ID );
        if ( mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID )
        {
            Log.e( "FlickrWidgetConfigure: Error bad appWidgetId" );
            finish();
            return;
        }
        Log.d( "FlickrWidgetConfigure: widget app id = " + mAppWidgetId );

        /* push the authentification keys to the library */
        AuthenticateActivity.registerAppParameters( this, getString( R.string.api_key ),
                                                    getString( R.string.api_secret ), getString( R.string.auth_url ) );
        RestClient.setAuth( this );

        mFlickrLibraryPrefs = getSharedPreferences( GlobalResources.PREFERENCES_ID, 0 );

        /* check the authentification */
        if ( APICalls.authCheckToken() )
        {
            /* auth OK, we display the configuration layout */
            displayConfigureLayout();
        }
        else
        {
            /* auth need to be done, we display the connect button */
            setContentView( R.layout.connect );
        }
        Log.d( "FlickrWidgetConfigure: End onCreate" );
    }

    /**
     * display the configuration layout, fill some attributes and textviews.
     */
    private void displayConfigureLayout()
    {
        setContentView( R.layout.widget_configure );
        mCheckBoxUserComments = (CheckBox) findViewById( R.id.checkbox_userComments );
        mCheckBoxUserComments.setOnClickListener( this );
        mCheckBoxUserPhotos = (CheckBox) findViewById( R.id.checkbox_userPhotos );
        mCheckBoxUserPhotos.setOnClickListener( this );
        mCommitButton = (Button) findViewById( R.id.button_commit );

        /* display the userName */
        String userName = mFlickrLibraryPrefs.getString( GlobalResources.PREF_USERNAME, null );
        Button button = (Button) findViewById( R.id.connected_to_flickr );
        button.setText( getString( R.string.connected_to_flickr, userName ) );
    }

    /**
     * Starts the flickr authentification
     * @param view
     */
    public void onConnect( View view )
    {
        startActivityForResult( new Intent( this, AuthenticateActivity.class ), AUTHENTICATE );
    }

    /**
     * Disconnect from Flickr
     * @param view
     */
    public void onDisconnect( View view )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.alert_disconnect ).setCancelable( false )
            .setPositiveButton( R.string.yes, new DialogInterface.OnClickListener()
            {
                public void onClick( DialogInterface dialog, int id )
                {
                    Toast.makeText( FlickrWidgetConfigure.this, R.string.disconnect_ok, Toast.LENGTH_SHORT ).show();
                    AuthenticateActivity.LogOut( mFlickrLibraryPrefs );
                    FlickrWidgetConfigure.this.setContentView( R.layout.connect );
                }
            } ).setNegativeButton( R.string.no, new DialogInterface.OnClickListener()
            {
                public void onClick( DialogInterface dialog, int id )
                {
                    dialog.cancel();
                }
            } );
        builder.create().show();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );
        Log.d( "FlickrWidgetConfigure: Start onActivityResult" );
        switch ( requestCode )
        {
            case AUTHENTICATE:
                if ( resultCode == AuthenticateActivity.AUTH_SUCCESS )
                {
                    Toast.makeText( this, R.string.connectOK, Toast.LENGTH_SHORT ).show();
                    RestClient.setAuth( this );
                    displayConfigureLayout();
                }
                else
                {
                    Toast.makeText( this, R.string.connectKO, Toast.LENGTH_SHORT ).show();
                }
                break;
        }
        Log.d( "FlickrWidgetConfigure: end onActivityResult" );
    }

    /**
     * @param context
     * @param appWidgetId
     * @return
     */
    public static WidgetConfiguration loadConfiguration( Context context, int appWidgetId )
    {
        SharedPreferences widgetPrefs = context.getSharedPreferences( Constants.WIDGET_PREFS, 0 );
        WidgetConfiguration widgetConfiguration = new WidgetConfiguration();
        widgetConfiguration.setShowUserComments( widgetPrefs.getBoolean( Constants.WIDGET_SHOW_USERCOMMENTS
            + appWidgetId, false ) );
        widgetConfiguration.setShowUserPhotos( widgetPrefs.getBoolean( Constants.WIDGET_SHOW_USERPHOTOS + appWidgetId,
                                                                       false ) );
        return widgetConfiguration;
    }

    /**
     * @param context
     * @param appWidgetId
     * @return
     */
    public static void saveConfiguration( Context context, int appWidgetId, WidgetConfiguration widgetConfiguration )
    {
        SharedPreferences widgetPrefs = context.getSharedPreferences( Constants.WIDGET_PREFS, 0 );
        Editor editor = widgetPrefs.edit();
        editor.putBoolean( Constants.WIDGET_SHOW_USERCOMMENTS + appWidgetId, widgetConfiguration.isShowUserComments() );
        editor.putBoolean( Constants.WIDGET_SHOW_USERPHOTOS + appWidgetId, widgetConfiguration.isShowUserPhotos() );
        editor.commit();
    }

    public WidgetConfiguration generateWidgetConfiguration()
    {
        WidgetConfiguration widgetConfiguration = new WidgetConfiguration();
        widgetConfiguration.setShowUserComments( mCheckBoxUserComments.isChecked() );
        widgetConfiguration.setShowUserPhotos( mCheckBoxUserPhotos.isChecked() );
        return widgetConfiguration;
    }

    public void onConfigurationDone( View view )
    {
        /* save the configuration */
        WidgetConfiguration widgetConfiguration = generateWidgetConfiguration();
        saveConfiguration( this, mAppWidgetId, widgetConfiguration );

        /* now the widget need to be manually updated */
        Log.d( "FlickrWidgetConfigure: Start Widget update with id" + mAppWidgetId );

        WidgetUpdateService.start( this, mAppWidgetId );

        Intent resultValue = new Intent();
        resultValue.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId );
        setResult( RESULT_OK, resultValue );
        Log.d( "FlickrWidgetConfigure: Widget updated" );
        finish();
    }

    public void onCancel( View view )
    {
        finish();
    }

    private boolean isConfigurationOk()
    {
        return mCheckBoxUserComments.isChecked() || mCheckBoxUserPhotos.isChecked();
    }

    /**
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick( View v )
    {
        mCommitButton.setEnabled( isConfigurationOk() );
    }
}
