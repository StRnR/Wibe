package com.mahaventures.wibe.Models.NewModels.ProfileModels;

import android.content.Context;
import android.os.Build;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mahaventures.wibe.BuildConfig;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.Locale;
import java.util.UUID;

public class SignInRequestModel {
    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("os")
    @Expose
    public String os;

    @SerializedName("os_version")
    @Expose
    private String os_version;

    @SerializedName("device_type")
    @Expose
    private String device_type;

    @SerializedName("device_name")
    @Expose
    private String device_name;

    @SerializedName("app_version")
    @Expose
    private String app_version;

    @SerializedName("store")
    @Expose
    private String store;

    public SignInRequestModel(String email, String password, Context context) {
        this.email = email;
        this.password = password;
        uuid = UUID.randomUUID().toString();
        app_version = BuildConfig.VERSION_NAME;
        os = "Android";
        device_name = StaticTools.getDeviceName();
        device_type = StaticTools.getDeviceType(context);
        os_version = String.format(Locale.getDefault(), "%s- sdk:%d", Build.VERSION.CODENAME, Build.VERSION.SDK_INT);
        store = StaticTools.getStore(context);
    }
}
