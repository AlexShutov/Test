package com.example.lodoss.test;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.lodoss.test.archframework.PresenterImpl;
import com.example.lodoss.test.archframework.UpdatableView;
import com.example.lodoss.test.databinding.ActivityMainBinding;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static android.view.View.GONE;
import static com.example.lodoss.test.SieveModel.*;
import static com.example.lodoss.test.SieveModel.SieveUserActionEnum.*;

public class MainActivity extends AppCompatActivity implements
        UpdatableView<SieveModel, SieveQueryEnum, SieveUserActionEnum> {
    // time in milliseconds between consecutive algorithm launches
    private static final long USER_INPUT_DEBOUNCE_TIME = 100;

    private UserActionListener userActionListener;
    // databinding for this screen
    private ActivityMainBinding viewBinding;

    private NumbersAdapter listAdapter;

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
                BigInteger sum = model.getSum();
                if (numbers == null){
                    showToast("List of numbers is empty");
                    return;
                } else {
                    showSum(sum);
                    listAdapter.setNumbers(numbers);
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // initi UI
        hideSumArea();
        listAdapter = new NumbersAdapter(this);
        viewBinding.listNumbers.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.listNumbers.setAdapter(listAdapter);
        initMVP();
        initHandlingUserInput();
    }

    private void initHandlingUserInput(){
        RxTextView.textChanges(viewBinding.maxNumberEntry)
                .skip(1)
                .debounce(USER_INPUT_DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                    handleEnteredValue(String.valueOf(text));
                });
    }

    /**
     * Verify if entered value is valid and change visibility of 'convert' button
     * accordingly.
     * @param input
     */
    private void handleEnteredValue(String input){
        TextInputLayout entryLayout = viewBinding.maxNumberEntryLayout;
        BigInteger num = null;
        try {
            num = new BigInteger(input);
        } catch (NumberFormatException e){
            // user entered incorrect value
            entryLayout.setError(getString(R.string.text_error_illegal_number_format));
            hideSumArea();
            hideNumbersList();
            return;
        }
        // negative value
        if (num.compareTo(BigInteger.ZERO) < 0){
            entryLayout.setError(getString(R.string.text_enter_negative));
            hideSumArea();
            hideNumbersList();
            return;
        }
        // user input is correct
        entryLayout.setError("");
        // start computing sequence
        showSumArea();
        showNumbersList();
        Bundle args = new Bundle();
        args.putString(SieveModel.KEY_LIMIT_NUMBER, num.toString());
        userActionListener.onUserAction(COMPUTE_NUMBERS_LESS_THAN, args);
    }

    private void initMVP(){
        SieveApplication app = (SieveApplication) getApplication();
        SieveModel model = app.getSieveModel();
        PresenterImpl presenter = new PresenterImpl(model, this, values(),
                SieveQueryEnum.values());
        addListener(presenter);
    }

    private void hideSumArea(){
        viewBinding.layoutSum.setVisibility(GONE);
    }

    private void showSumArea(){
        viewBinding.layoutSum.setVisibility(View.VISIBLE);
    }

    private void showNumbersList(){
        viewBinding.listNumbers.setVisibility(View.VISIBLE);
    }

    private void hideNumbersList(){
        viewBinding.listNumbers.setVisibility(View.GONE);
    }

    private void showSum(BigInteger sum){
        if (null == sum){
            hideSumArea();
            return;
        }
        showNumbersList();
        viewBinding.textSum.setText(sum.toString());
        viewBinding.layoutSum.setVisibility(View.VISIBLE);
    }

    private void showToast(final String message){
        Handler h = new Handler(Looper.getMainLooper());
        h.post(() -> {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}
