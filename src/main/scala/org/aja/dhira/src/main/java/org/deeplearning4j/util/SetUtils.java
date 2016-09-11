/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package org.deeplearning4j.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetUtils
{
	private SetUtils() {}

	// Set specific operations

	public static <T> Set<T> intersection(Collection<T> parentCollection, Collection<T> removeFromCollection)
	{
		Set<T> results = new HashSet<>(parentCollection) ;
		results.retainAll(removeFromCollection) ;
		return results ;
	}

	public static <T> boolean intersectionP(Set<? extends T> s1, Set<? extends T> s2)
	{
		for( T elt : s1 )
		{
			if ( s2.contains(elt) ) 
				return true ;
		}
		return false ;
	}

	public static <T> Set<T> union(Set<? extends T> s1, Set<? extends T> s2)
	{
		Set<T> s3 = new HashSet<>(s1) ;
		s3.addAll(s2) ;
		return s3 ;
	}

	/** Return is s1 \ s2 */

	public static <T> Set<T> difference(Collection<? extends T> s1, Collection<? extends T> s2)
	{
		Set<T> s3 = new HashSet<>(s1) ;
		s3.removeAll(s2) ;
		return s3 ;
	}
}


