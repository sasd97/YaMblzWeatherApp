package me.grechka.yamblz.yamblzweatherapp.activity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import dalvik.annotation.TestTargetClass;

/**
 * Created by Grechka on 15.07.2017.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface MainView extends MvpView {

    @StateStrategyType(SingleStateStrategy.class)
    void showWeather();

    void showSettings();

    void showAbout();

    @StateStrategyType(SkipStrategy.class)
    void navigate(int id);

    void goBack();

}
