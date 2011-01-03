/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.beans;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class.<p>
 *
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement PropertyChangeListener (when the value
 * is a closue defined in place) or be casted as a proxy of PropertyChangeListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @see org.codehaus.griffon.ast.ListenerASTTransformation
 *
 * @author Andres Almiray
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE})
@GroovyASTTransformationClass("org.codehaus.griffon.ast.ListenerASTTransformation")
public @interface Listener {
    String value();
}