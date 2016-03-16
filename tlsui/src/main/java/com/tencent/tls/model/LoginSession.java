package com.tencent.tls.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.tencent.tls.service.Constants;

import tencent.tls.request.Ticket;

/**
 * Created by markding on 2015/11/27.
 */
public class LoginSession implements Parcelable{

    private int loginWay = Constants.NON_LOGIN;
    private String identifer;
    private String openid;
    private String access_token;

    public LoginSession(){}

    public int getLoginWay() {
        return loginWay;
    }

    public void setLoginWay(int loginWay) {
        this.loginWay = loginWay;
    }

    public String getIdentifer() {
        return identifer;
    }

    public void setIdentifer(String identifer) {
        this.identifer = identifer;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(loginWay);
        dest.writeString(identifer);
        dest.writeString(openid);
        dest.writeString(access_token);
    }

    public void readFromParcel(Parcel in) {
        loginWay = in.readInt();
        identifer = in.readString();
        openid = in.readString();
        access_token = in.readString();
    }

    private LoginSession(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<LoginSession> CREATOR = new Creator<LoginSession>() {

        @Override
        public LoginSession createFromParcel(Parcel source) {
            return new LoginSession(source);
        }

        @Override
        public LoginSession[] newArray(int size) {
            return new LoginSession[size];
        }
    };
}
