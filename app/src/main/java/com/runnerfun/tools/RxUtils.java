package com.runnerfun.tools;

import rx.Subscription;

public class RxUtils {

    public static void unSubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

}
