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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kamosoft.flickr.APICalls;
import com.kamosoft.flickr.AuthenticateActivity;
import com.kamosoft.flickr.GlobalResources;
import com.kamosoft.flickr.GlobalResources.ImgSize;
import com.kamosoft.flickr.RestClient;
import com.kamosoft.flickr.model.Event;
import com.kamosoft.flickr.model.Item;
import com.kamosoft.flickr.model.JsonFlickrApi;
import com.kamosoft.flickr.model.Photo;

/**
 * @author Tom
 * created 15 mars 2011
 */
public class FlickrConnectActivity
    extends Activity
{

    private static final int AUTHENTICATE = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.connect );

        AuthenticateActivity.registerAppParameters( this, getString( R.string.api_key ),
                                                    getString( R.string.api_secret ), getString( R.string.auth_url ) );
        RestClient.setAuth( this );

        Button connectButton = (Button) findViewById( R.id.connect_button );
        SharedPreferences prefs = getSharedPreferences( GlobalResources.PREFERENCES_ID, 0 );
        String userId = prefs.getString( GlobalResources.PREF_USERID, null );

        /* check if flickr connect is still valid */
        if ( APICalls.authCheckToken() && userId != null )
        {

            //showActivityUserPhotos( userId );
            connectButton.setEnabled( false );
        }
        else
        {
            //          AuthenticateActivity.LogOut( prefs );
            //            AuthenticateActivity
            //                .registerAppParameters( this, getString( R.string.api_key ), getString( R.string.api_secret ),
            //                                        getString( R.string.auth_url ) );
            connectButton.setEnabled( true );
        }
    }

    private void showActivityUserPhotos( String userId )
    {
        try
        {
            JsonFlickrApi jsApi = APICalls.getActivityUserPhotos( userId, "5d", "", "" );
            Log.i( jsApi.toString() );

            LinearLayout mainLayout = (LinearLayout) findViewById( R.id.main_layout );

            for ( Item item : jsApi.getItems().getItems() )
            {
                View child = null;
                switch ( item.getType() )
                {
                    case photo:
                        child = getLayoutInflater().inflate( R.layout.item_photo, null );
                        TextView photoText = (TextView) child.findViewById( R.id.photoText );
                        LinearLayout eventsLayout = (LinearLayout) child.findViewById( R.id.events );

                        ImageView imageView = (ImageView) child.findViewById( R.id.photoBitmap );
                        Photo photo = APICalls.getPhotoInfo( item.getId() ).getPhoto();
                        imageView.setImageBitmap( GlobalResources.getBitmapFromURL( photo, ImgSize.SMALLSQUARE ) );

                        photoText.setText( item.getTitle().getContent() );

                        for ( Event event : item.getActivity().getEvents() )
                        {
                            TextView eventText = new TextView( this );
                            switch ( event.getType() )
                            {
                                case added_to_gallery:
                                    eventText.setText( "added to gallery by " + event.getUsername() );
                                    break;

                                case comment:
                                    eventText.setText( event.getContent() );
                                    break;

                                case fave:
                                    eventText.setText( "added to " + event.getUsername() + "'s favorites" );
                                    break;

                                default:
                                    Log.e( "unhandled event Type : " + event.getType() );
                                    eventText.setText( "unhandled event Type : " + event.getType() );
                            }
                            eventsLayout.addView( eventText );
                        }

                    default:
                        Log.e( "unhandled Item Type : " + item.getType() );
                }

                if ( child != null )
                {
                    mainLayout.addView( child );
                }
            }
        }
        catch ( Exception e )
        {
            Log.e( e.getMessage() );
        }
    }

    public void onConnect( View view )
    {
        startActivityForResult( new Intent( this, AuthenticateActivity.class ), AUTHENTICATE );
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
                    String userId = getSharedPreferences( GlobalResources.PREFERENCES_ID, 0 )
                        .getString( GlobalResources.PREF_USERID, null );
                    RestClient.setAuth( this );
                    showActivityUserPhotos( userId );
                }
                else
                {
                    Toast.makeText( this, R.string.connectKO, Toast.LENGTH_SHORT ).show();
                }
                break;
        }
    }

}
