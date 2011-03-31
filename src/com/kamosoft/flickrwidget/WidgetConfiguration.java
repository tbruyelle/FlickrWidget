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

import java.io.Serializable;

/**
 * @author Tom
 * created 15 mars 2011
 */
public class WidgetConfiguration
    implements Serializable
{
    public enum Content {
        userPhotos, userComments
    };

    private Content content;

    private int maxItems = 10;

    public WidgetConfiguration( Content content, int maxItem )
    {
        this.content = content;
        this.setMaxItems( maxItem );
    }

    public WidgetConfiguration()
    {

    }

    /**
     * @return the content
     */
    public Content getContent()
    {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent( Content content )
    {
        this.content = content;
    }

    /**
     * @param content the content to set
     */
    public void setContent( String content )
    {
        if ( content != null )
        {
            this.content = Content.valueOf( content );
        }
    }

    public boolean isDisplayable()
    {
        return content != null;
    }

    /**
     * @param maxItems the maxItem to set
     */
    public void setMaxItems( int maxItems )
    {
        this.maxItems = maxItems;
    }

    /**
     * @return the maxItem
     */
    public int getMaxItems()
    {
        return maxItems;
    }

}
