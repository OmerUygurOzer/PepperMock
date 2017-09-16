package com.example.omer.pepperapp;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Omer on 9/14/2017.
 */

public class Bus {

    private static Bus bus;

    private static Bus getInstance(){
        if(bus == null){
            bus = new Bus();
        }
        return bus;
    }

    public static void subscribeToEvent(String code, Listener listener){
        getInstance().addListener(code,listener);
    }

    public static void post(String code, Object object){
        getInstance().postEvent(code,object);
    }

    private Map<String,Set<ListenerWrapper>> codeToListenerMap;

    private Bus(){
        codeToListenerMap = new LinkedHashMap<>();
    }

    private void addListener(String code, Listener listener){
        if(!codeToListenerMap.containsKey(code)){
            codeToListenerMap.put(code,new HashSet<ListenerWrapper>());
        }
        codeToListenerMap.get(code).add(wrap(listener));
    }

    private void postEvent(String code, Object event){
        for(ListenerWrapper wrapper: codeToListenerMap.get(code)){
            wrapper.postEvent(code,event);
        }
    }

    private ListenerWrapper wrap(Listener listener){
        return new ListenerWrapper(listener);
    }

    private class ListenerWrapper{

        private WeakReference<Listener> listenerWeakRef;

        private ListenerWrapper(Listener listener){
            listenerWeakRef = new WeakReference<>(listener);
        }

        private void postEvent(String code,Object event){
            if(listenerWeakRef.get()!=null){
                listenerWeakRef.get().eventReceived(code,event);
            }
        }

    }

    public interface Listener{
        void eventReceived(String code,Object event);
    }
}
