package com.example.lodoss.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lodoss.test.archframework.Model;
import com.example.lodoss.test.archframework.QueryEnum;
import com.example.lodoss.test.archframework.UserActionEnum;
import com.example.lodoss.test.sieve.InfiniteSieve;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Alex on 5/5/2017.
 */

public class SieveModel implements Model<SieveModel.SieveQueryEnum, SieveModel.SieveUserActionEnum> {

    private static final String LOG_TAG = SieveModel.class.getSimpleName();

    private Context context;
    private List<BigInteger> numbers;
    private BigInteger sum;
    /**
     * Connection to the active compuration algorithm. Suppose, it can take a long time to
     * complete
     */
    private Subscription computationAlgorithmSubscription;

    public SieveModel(Context context){
        this.context = context;
    }

    /**
     * Key for storing maximal number in Bundle while sending user action request
     */
    public static final String KEY_LIMIT_NUMBER = "limit_number";

    public static BigInteger computeSum(Iterable<BigInteger> values){
        BigInteger sum = new BigInteger("0");
        for (BigInteger n : values){
            sum = sum.add(n);
        }
        return sum;
    }

    @Override
    public SieveQueryEnum[] getQueries() {
        return SieveQueryEnum.values();
    }

    @Override
    public SieveUserActionEnum[] getUserActions() {
        return SieveUserActionEnum.values();
    }

    /**
     * Process user action here
     * @param action
     * @param args
     * @param callback
     */
    @Override
    public void deliverUserAction(final SieveUserActionEnum action, @Nullable Bundle args,
                                  final UserActionCallback callback) {
        switch (action){
            case COMPUTE_NUMBERS_LESS_THAN:

                stopAlgorithmIfRunning();
                final String maxNumberStr = args.getString(KEY_LIMIT_NUMBER);
                computationAlgorithmSubscription =
                        Observable.just(maxNumberStr)
                        .subscribeOn(Schedulers.computation())
                                // do all the work
                        .map(stringVal -> {
                            BigInteger maxNumber = new BigInteger(stringVal);
                            List<BigInteger> numbers =
                                    generateNumbersLessThan(new InfiniteSieve(), maxNumber);
                            BigInteger sum = computeSum(numbers);
                            // save computed values to the model.
                            // Do so as atomic operation, even though we keep only
                            // one algorithm by managing Subscriptoin
                            synchronized (SieveModel.this){
                                SieveModel.this.numbers = numbers;
                                SieveModel.this.sum = sum;
                            }
                            return sum;
                        })
                        // notify UI
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> {
                            callback.onModelUpdated(SieveModel.this, action);
                        }, e -> {
                            callback.onError(action);
                        });
                break;
            default:
        }
    }

    /**
     * Process data request (usually called at the first call, when program is loaded
     * @param query
     * @param callback
     */
    @Override
    public void requestData(SieveQueryEnum query, DataQueryCallback callback) {
    }

    /**
     * Clear previous value and cancel any pending computation algorithm
     */
    @Override
    public void cleanUp() {
        numbers = null;
        sum = null;
        stopAlgorithmIfRunning();
    }

    /**
     * Unsubscribe from subscription of running algorithm.
     * It is more preferable to use custom thread we can stop.
     */
    private void stopAlgorithmIfRunning(){
        if (null != computationAlgorithmSubscription &&
                !computationAlgorithmSubscription.isUnsubscribed()){
            Log.w(LOG_TAG, "There is a pending computation, cancelling it");
            computationAlgorithmSubscription.unsubscribe();
            computationAlgorithmSubscription = null;
        }
    }

    /**
     * Generate list of prime numbers, not exceeding maximal value.
     * @param sieve reference to the algorithm
     * @param max
     * @return
     */
    private List<BigInteger> generateNumbersLessThan(InfiniteSieve sieve, BigInteger max){
        List<BigInteger> numbers = new ArrayList<>();
        BigInteger n = sieve.next();
        while (n.compareTo(max) < 0){
            numbers.add(n);
            n = sieve.next();
        }
        return numbers;
    }

    public enum SieveQueryEnum implements QueryEnum {

        COMPUTE_NUMBERS(0);

        int id;

        SieveQueryEnum(int id){
            this.id = id;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String[] getProjection() {
            return new String[0];
        }
    }

    public enum SieveUserActionEnum implements UserActionEnum {

        /**
         * Send this user action for telling Model to compute list of all prime numbers, less than
         * a particular number, passed unnder key KEY_LIMIT_NUMBER
         */
        COMPUTE_NUMBERS_LESS_THAN(0);

        private int id;

        SieveUserActionEnum(int id){
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public List<BigInteger> getNumbers() {
        return numbers;
    }

    public BigInteger getSum() {
        return sum;
    }
}
