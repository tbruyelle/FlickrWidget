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

/**
 * @author Tom
 * created 15 mars 2011
 */
public class Log
{
    public final static String LOGTAG = "FlickrWidget";

    public static final boolean DEBUG = true;

    public static void v( String msg )
    {
        android.util.Log.v( LOGTAG, msg );
    }

    public static void d( String msg )
    {
        if ( DEBUG )
        {
            android.util.Log.d( LOGTAG, msg );
        }
    }

    public static void e( String msg )
    {
        android.util.Log.e( LOGTAG, msg );
    }

    public static void i( String msg )
    {
        android.util.Log.i( LOGTAG, msg );
    }

    public static void e( String msg, Throwable tr )
    {
        android.util.Log.e( LOGTAG, msg, tr );
    }

    public static void w( String msg )
    {
        android.util.Log.w( LOGTAG, msg );
    }

    public static void w( String msg, Throwable tr )
    {
        android.util.Log.w( LOGTAG, msg, tr );
    }
}
