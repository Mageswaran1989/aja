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


import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An index is a transform of objects augmented with a list and a reverse lookup table
 * for fast lookups.
 * Indices are used for vocabulary in many of the natural language processing
 * @author Adam Gibson
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class Index implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1160629777026141078L;
    Map<Integer,Object> objects = new ConcurrentHashMap<>();

    Map<Object,Integer> indexes = new ConcurrentHashMap<>();

    public synchronized boolean add(Object o,int idx) {
        if(o instanceof String && o.toString().isEmpty()) {
            throw new IllegalArgumentException("Unable to add the empty string");
        }

        Integer index = indexes.get(o);
        if (index == null) {
            index = idx;
            objects.put(idx,o);
            indexes.put(o, index);
            return true;
        }
        return false;
    }

    public synchronized boolean add(Object o) {
        if(o instanceof String && o.toString().isEmpty()) {
            throw new IllegalArgumentException("Unable to add the empty string");
        }
        Integer index = indexes.get(o);
        if (index == null) {
            index = objects.size();
            objects.put(index,o);
            indexes.put(o, index);
            return true;
        }
        return false;
    }

    public synchronized int indexOf(Object o) {
        Integer index = indexes.get(o);
        if (index == null) { return -1; }
        else { return index; }
    }

    public synchronized Object get(int i) {
        return objects.get(i);
    }

    public int size() {
        return objects.size();
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("[");
        int sz = objects.size();
        int i;
        for (i = 0; i < sz; i++) {
            Object e = objects.get(i);
            buff.append(e);
            if (i < (sz-1)) buff.append(" , ");
        }
        buff.append("]");
        return buff.toString();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Index index = (Index) o;

        if (objects != null ? !objects.equals(index.objects) : index.objects != null) return false;
        return !(indexes != null ? !indexes.equals(index.indexes) : index.indexes != null);

    }

    @Override
    public int hashCode() {
        int result = objects != null ? objects.hashCode() : 0;
        result = 31 * result + (indexes != null ? indexes.hashCode() : 0);
        return result;
    }
}