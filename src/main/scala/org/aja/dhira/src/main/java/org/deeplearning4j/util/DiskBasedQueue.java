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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Naive disk based queue for storing items on disk.
 * Only meant for poll and adding items.
 * @author Adam Gibson
 */
public class DiskBasedQueue<E> implements Queue<E>,Serializable {

    private File dir;
    private Queue<String> paths = new ConcurrentLinkedDeque<>();
    private ScheduledExecutorService executorService;
    private AtomicBoolean running = new AtomicBoolean(true);
    private Queue<E> save = new ConcurrentLinkedDeque<>();

    public DiskBasedQueue() {
        this(".queue");
    }

    public DiskBasedQueue(String path) {
        this(new File(path));

    }

    public DiskBasedQueue(File dir) {
        this.dir = dir;
        if(!dir.exists() && dir.isDirectory()) {
            throw new IllegalArgumentException("Illegal queue: must be a directory");
        }

        if(!dir.exists())
            dir.mkdirs();
        if(dir.listFiles() != null && dir.listFiles().length > 1)
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }


        dir.mkdir();


        executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
               while(running.get()) {
                while(!save.isEmpty())
                    addAndSave(save.poll());
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       Thread.currentThread().interrupt();
                   }
               }
            }
        });
    }

    @Override
    public int size() {
        return paths.size();
    }

    @Override
    public boolean isEmpty() {
        return paths.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();

    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();

    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E e) {
        save.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for(E e : c)
            addAndSave(e);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E poll() {
        String path = paths.poll();
        E ret = SerializationUtils.readObject(new File(path));
        File item = new File(path);
        item.delete();
        return ret;
    }

    @Override
    public E element() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException();
    }




    private void addAndSave(E e) {
        File path = new File(dir,UUID.randomUUID().toString());
        SerializationUtils.saveObject(e,path);
        paths.add(path.getAbsolutePath());
    }


}
