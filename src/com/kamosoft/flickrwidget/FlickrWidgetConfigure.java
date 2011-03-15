/**
 * Copyright - Accor - All Rights Reserved www.accorhotels.com
 */
package com.kamosoft.flickrwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kamosoft.flickr.APICalls;
import com.kamosoft.flickr.AuthenticateActivity;
import com.kamosoft.flickr.GlobalResources;
import com.kamosoft.flickr.RestClient;

/**
 * The widget configuration activity.
 * Perform the flickr authentification and other stuff
 * @author <a href="mailto:thomas.bruyelle@accor.com">tbruyelle</a>
 * created 10 mars 2011
 * @since 
 * @version $Id$
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

        /* set the Activity result to RESULT_CANCELED. 
         * This way, if the user backs-out of the Activity before reaching the end, 
         * the App Widget host is notified that the configuration was cancelled and the App Widget will not be added. */
        setResult( RESULT_CANCELED );

        /* retrieve the widget id */
        mAppWidgetId = getIntent().getExtras().getInt( AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                       AppWidgetManager.INVALID_APPWIDGET_ID );

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
    }

    public void onConfigurationDone( View view )
    {
        /* now the widget need to be manually updated */
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( this );

        RemoteViews views = new RemoteViews( this.getPackageName(), R.layout.appwidget );
        appWidgetManager.updateAppWidget( mAppWidgetId, views );
        Intent resultValue = new Intent();
        resultValue.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId );
        setResult( RESULT_OK, resultValue );
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
