/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package griffon.util;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class.<p>
 *
 * When annotating a class it indicates that it will become an
 * event publishing one. The class will have tha ability to send
 * arbitratry events to any listeners that may have been registered
 * with it. These events are similar to the ones published by
 * GriffonApplication.
 *
 * //TODO discuss generated fields and methods
 *
 * @see org.codehaus.griffon.ast.EventPublisherASTTransformation
 *
 * @author Andres Almiray
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass("org.codehaus.griffon.ast.EventPublisherASTTransformation")
public @interface EventPublisher {
}
