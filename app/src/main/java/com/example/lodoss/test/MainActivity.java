package com.example.lodoss.test;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lodoss.test.archframework.PresenterImpl;
import com.example.lodoss.test.archframework.UpdatableView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.example.lodoss.test.SieveModel.*;
import static com.example.lodoss.test.SieveModel.SieveUserActionEnum.*;

public class MainActivity extends AppCompatActivity implements
        UpdatableView<SieveModel, SieveQueryEnum, SieveUserActionEnum> {

    private UserActionListener userActionListener;

    @Override
    public Uri getDataUri(SieveQueryEnum query) {
        return null;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void addListener(UserActionListener listener) {
        userActionListener = listener;
    }

    @Override
    public void displayErrorMessage(SieveQueryEnum query) {
        switch (query){
            case COMPUTE_NUMBERS:
                showToast("Error, cannot comput numbers");
                break;
            default:
        }
    }

    @Override
    public void displayData(SieveModel model, SieveQueryEnum query) {
    }

    @Override
    public void displayUserActionResult(SieveModel model,
                                        SieveUserActionEnum userAction,
                                        boolean success) {
        if (!success){
            showToast("request failed");
            return;
        }
        switch (userAction){
            case COMPUTE_NUMBERS_LESS_THAN:
                List<BigInteger> numbers = model.getNumbers();
                if (numbers == null){
                    showToast("List of numbers is empty");
                    return;
                } else {
                    showToast("There are " + numbers.size() + " numbers");
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMVP();

        BigDecimal max = new BigDecimal("1234");
        Bundle args = new Bundle();
        args.putString(SieveModel.KEY_LIMIT_NUMBER, max.toPlainString());
        userActionListener.onUserAction(COMPUTE_NUMBERS_LESS_THAN, args);
    }

    private void initMVP(){
        SieveApplication app = (SieveApplication) getApplication();
        SieveModel model = app.getSieveModel();
        PresenterImpl presenter = new PresenterImpl(model, this, values(),
                SieveQueryEnum.values());
        addListener(presenter);

    }

    private void showToast(final String message){
        Handler h = new Handler(Looper.getMainLooper());
        h.post(() -> {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}
