package com.echo.common.util.retrofit;

import rx.Observable.Operator;
import rx.Subscriber;

/**
 * Created by jiangecho on 16/3/26.
 */
public class RetrofitSericalConcat<T, R> implements Operator<T, R> {
    public RetrofitSericalConcat( /* any necessary params here */) {
    /* any necessary initialization here */
    }

    public Subscriber<? super R> call(final Subscriber<? super T> s) {
        return new Subscriber<R>(s) {
            @Override
            public void onCompleted() {
        /* add your own onCompleted behavior here, or just pass the completed notification through: */
                if (!s.isUnsubscribed()) {
                    s.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
        /* add your own onError behavior here, or just pass the error notification through: */
                if (!s.isUnsubscribed()) {
                    s.onError(t);
                }
            }

            @Override
            public void onNext(R r) {
        /* this example performs some sort of operation on each incoming item and emits the results */
                if (!s.isUnsubscribed()) {
//                    transformedItem = myOperatorTransformOperation(item);
//                    s.onNext(transformedItem);
                }

            }
        };
    }


}
