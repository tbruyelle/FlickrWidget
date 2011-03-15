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
public class WidgetConfiguration
{
    private boolean showUserPhotos;

    private boolean showUserComments;

    public WidgetConfiguration( boolean showUserPhotos, boolean showUserComments )
    {
        this.showUserPhotos = showUserPhotos;
        this.showUserComments = showUserComments;
    }

    public WidgetConfiguration()
    {

    }

    /**
     * @return the displayUserPhotos
     */
    public boolean isShowUserPhotos()
    {
        return showUserPhotos;
    }

    /**
     * @param displayUserPhotos the displayUserPhotos to set
     */
    public void setShowUserPhotos( boolean displayUserPhotos )
    {
        this.showUserPhotos = displayUserPhotos;
    }

    /**
     * @return the displayUserComments
     */
    public boolean isShowUserComments()
    {
        return showUserComments;
    }

    /**
     * @param displayUserComments the displayUserComments to set
     */
    public void setShowUserComments( boolean displayUserComments )
    {
        this.showUserComments = displayUserComments;
    }

}
