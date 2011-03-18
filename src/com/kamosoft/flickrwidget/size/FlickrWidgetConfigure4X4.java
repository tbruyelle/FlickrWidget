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
package com.kamosoft.flickrwidget.size;

import com.kamosoft.flickrwidget.Constants;
import com.kamosoft.flickrwidget.FlickrWidgetConfigure;

/**
 * @author Tom
 * created 18 mars 2011
 */
public class FlickrWidgetConfigure4X4
    extends FlickrWidgetConfigure
{
    /**
     * @see com.kamosoft.flickrwidget.FlickrWidgetConfigure#getMaxItems()
     */
    @Override
    protected int getMaxItems()
    {
        return Constants.MAX_ITEMS_FOR_4X4;
    }
}
