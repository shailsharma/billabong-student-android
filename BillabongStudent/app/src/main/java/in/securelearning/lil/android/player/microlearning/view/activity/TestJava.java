package in.securelearning.lil.android.player.microlearning.view.activity;

import android.util.Log;

/**
 * Created by Chaitendra on 20-Feb-18.
 */

public class TestJava {
    public static void main(String[] args) {
        MutualFund mutualFund = new HDFC();
        Log.e("ROI--", String.valueOf(mutualFund.rateOfInterest()));
    }

}

interface MutualFund {
    float rateOfInterest();
}

class HDFC implements MutualFund {

    @Override
    public float rateOfInterest() {
        return (float) 18.38;
    }
}

class ICICI implements MutualFund {

    @Override
    public float rateOfInterest() {
        return (float) 16.50;
    }
}
