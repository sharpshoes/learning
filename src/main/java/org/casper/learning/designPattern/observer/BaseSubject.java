package org.casper.learning.designPattern.observer;

import java.util.ArrayList;
import java.util.List;

public class BaseSubject {

    List<BaseObserver> observerList = new ArrayList<>();

    public void attach(BaseObserver observer) {
        this.observerList.add(observer);
    }

    public void disAttach(BaseObserver observer) {
        this.observerList.remove(observer);
    }

}
