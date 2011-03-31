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

import android.app.Dialog;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutDialog
    extends Dialog
{
    /**
     * @param context
     */
    public AboutDialog( Context context )
    {
        super( context );
        setContentView( R.layout.about );
        setTitle( context.getText( R.string.app_name ) + " " + context.getText( R.string.version ) );

        TextView link = (TextView) findViewById( R.id.about_content );
        link.setMovementMethod( LinkMovementMethod.getInstance() );

        Button ok = (Button) findViewById( R.id.ok_button );
        ok.setOnClickListener( new View.OnClickListener()
        {
            public void onClick( View v )
            {
                AboutDialog.this.dismiss();
            }
        } );
    }

}