package com.kamosoft.flickrwidget;

import com.kamosoft.flickr.APICalls;
import com.kamosoft.flickr.GlobalResources;
import com.kamosoft.flickr.GlobalResources.ImgSize;
import com.kamosoft.flickr.model.Item;
import com.kamosoft.flickr.model.JsonFlickrApi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class userPhotoService
    extends Service
{
    public static final String USERID_INTENT = "userId";

    public static final String LAYOUT_TOADD_ID_INTENT = "layoutToAddId";

    /**
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart( Intent intent, int startId )
    {
        super.onStart( intent, startId );
        
        String userId=intent.getStringExtra( USERID_INTENT );
        int layoutId = intent.getIntExtra( LAYOUT_TOADD_ID_INTENT, -1 );
        
        try
        {
            JsonFlickrApi jsApi = APICalls.getActivityUserPhotos( userId, "5d", "", "" );
            Log.i( "FlickrWidget", jsApi.toString() );

//            LinearLayout mainLayout = (LinearLayout) findViewById( layoutId );
//
//            for ( Item item : jsApi.getItems().getItems() )
//            {
//                View child = null;
//                switch ( item.getType() )
//                {
//                    case photo:
//                        child = getLayoutInflater().inflate( R.layout.item_photo, null );
//                        TextView photoText = (TextView) child.findViewById( R.id.photoText );
//                        TextView eventText = (TextView) child.findViewById( R.id.eventText );
//
//                        String imageUrl=GlobalResources.getImageURL( item.getFarm(), item.getServer(), item.getId(), item.getSecret(), ImgSize.SMALLSQUARE, item.gete )
//                        photoText.setText( item.getTitle().getContent() );
//                        eventText.setText( item.getActivity().getEvents().iterator().next().getContent() );
//
//                    default:
//                        Log.e( "FlickrWidget", "unhandled Item Type : " + item.getType() );
//                }
//
//                if ( child != null )
//                {
//                    mainLayout.addView( child );
//                }
//            }
        }
        catch ( Exception e )
        {
            Log.e( "FlickrWidget", e.getMessage() );
        }
    }

    @Override
    public IBinder onBind( Intent intent )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
