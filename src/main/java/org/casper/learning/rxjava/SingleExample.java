package org.casper.learning.rxjava;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.omg.CORBA.SystemException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleExample {

    public static void main(String args[]) {

        Flowable<int[]> flow = Flowable.range(1, 5)
                .map(v -> new int[]{v* v, v + v})
                .filter(v -> v[0] > 0);


        flow.subscribe(SingleExample::print);

        Flowable.just("www.baidu.com", "www.sina.com")
                .parallel()
                .map(item -> item.split("[.]"))
                .flatMap(items -> Flowable.fromArray(items))
                .filter(item -> {
                    try {
                        Thread.sleep(1);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return true;
                });

        Observable.just("Hello,world")
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return Observable.just("Oh,I change to NIHAO");
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                    }
                });
    }

    public static void print(int[] value) {
        System.out.println(value[0] + "  " + value[1]);
    }

    public static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
