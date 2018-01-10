/*
 * Copyright 2018 TheAndroidMonk
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gworks.richedittext.markups

/**
 * Represents an attributed markup or markup with parameters. It could be single
 * param (like the url in Link markup) or a set of params (like style params in Font markup).
 * Often set of params are wrapped in a single class and used as generic param `ATTR`
 * while implementing this interface and single param need not be wrapped.
 * @param <ATTR>
*/
interface AttributedMarkup<ATTR> : Markup {

    /**
     * The attributes of this [AttributedMarkup].
     */
    val attributes: ATTR

}
