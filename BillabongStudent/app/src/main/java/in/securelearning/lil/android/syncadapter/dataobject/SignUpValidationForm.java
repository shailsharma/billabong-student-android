package in.securelearning.lil.android.syncadapter.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chaitendra on 13-Nov-17.
 */

public class SignUpValidationForm implements Serializable {

    public static final int APPROVE = 1;
    public static final int DECLINE = -1;

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("industryType")
    @Expose
    private String mIndustryType;

    @SerializedName("productsConsumed")
    @Expose
    private String mProductsConsumed;

    @SerializedName("region")
    @Expose
    private String mRegion;

    @SerializedName("procuresProduct")
    @Expose
    private String mProcuresProduct;

    @SerializedName("salesRepresentative")
    @Expose
    private String mSalesRepresentative;

    @SerializedName("userType")
    @Expose
    private String mUserType;

    @SerializedName("customerSubscriptionType")
    @Expose
    private String mCustomerSubscriptionType;

    @SerializedName("comment")
    @Expose
    private String mComment;

    @SerializedName("status")
    @Expose
    private int mStatus;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIndustryType() {
        return mIndustryType;
    }

    public void setIndustryType(String industryType) {
        mIndustryType = industryType;
    }

    public String getProductsConsumed() {
        return mProductsConsumed;
    }

    public void setProductsConsumed(String productsConsumed) {
        mProductsConsumed = productsConsumed;
    }

    public String getRegion() {
        return mRegion;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    public String getProcuresProduct() {
        return mProcuresProduct;
    }

    public void setProcuresProduct(String procuresProduct) {
        mProcuresProduct = procuresProduct;
    }

    public String getSalesRepresentative() {
        return mSalesRepresentative;
    }

    public void setSalesRepresentative(String salesRepresentative) {
        mSalesRepresentative = salesRepresentative;
    }

    public String getUserType() {
        return mUserType;
    }

    public void setUserType(String userType) {
        mUserType = userType;
    }

    public String getCustomerSubscriptionType() {
        return mCustomerSubscriptionType;
    }

    public void setCustomerSubscriptionType(String customerSubscriptionType) {
        mCustomerSubscriptionType = customerSubscriptionType;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }
}
