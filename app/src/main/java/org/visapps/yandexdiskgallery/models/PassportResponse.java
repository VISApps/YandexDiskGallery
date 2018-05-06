package org.visapps.yandexdiskgallery.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class PassportResponse {


    @PrimaryKey(autoGenerate = true)
    private long databaseid;

    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("default_email")
    @Expose
    private String defaultEmail;
    @SerializedName("real_name")
    @Expose
    private String realName;
    @SerializedName("is_avatar_empty")
    @Expose
    private Boolean isAvatarEmpty;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("default_avatar_id")
    @Expose
    private String defaultAvatarId;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("old_social_login")
    @Expose
    private String oldSocialLogin;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("id")
    @Expose
    private String id;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Boolean getIsAvatarEmpty() {
        return isAvatarEmpty;
    }

    public void setIsAvatarEmpty(Boolean isAvatarEmpty) {
        this.isAvatarEmpty = isAvatarEmpty;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDefaultAvatarId() {
        return defaultAvatarId;
    }

    public void setDefaultAvatarId(String defaultAvatarId) {
        this.defaultAvatarId = defaultAvatarId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getOldSocialLogin() {
        return oldSocialLogin;
    }

    public void setOldSocialLogin(String oldSocialLogin) {
        this.oldSocialLogin = oldSocialLogin;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDatabaseid() {
        return databaseid;
    }

    public void setDatabaseid(long databaseid) {
        this.databaseid = databaseid;
    }
}
