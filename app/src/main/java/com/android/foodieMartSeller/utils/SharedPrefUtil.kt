package com.android.foodieMartSeller.utils

import android.app.Application
import com.android.foodieMartSeller.network.localstorage.LocalStorageKey
import com.android.foodieMartSeller.network.localstorage.SharedPreferenceHelper
import com.android.foodieMartSeller.network.localstorage.SharedPreferenceHelperImpl

import javax.inject.Inject

class SharedPrefUtil @Inject constructor(application: Application) {

    private val mSharePreferences: SharedPreferenceHelper =
        SharedPreferenceHelperImpl(application)

    /**
     * flag for check whether user logged in or not.
     */
    var isLoggedIn: Boolean = false
        get() {
            return mSharePreferences.getBoolean(LocalStorageKey.IS_LOGGED_IN.name, false)
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.IS_LOGGED_IN.name, value)
        }

    /**
     * auth key for headers.
     */
    var authKey: String = ""
        get() {
            return mSharePreferences.getString(LocalStorageKey.X_AUTH_TOKEN.name)
        }
        set(value) {
            field = value
            mSharePreferences.setString(LocalStorageKey.X_AUTH_TOKEN.name, value)
        }

    /**
     * Check if Device Already Registered.
     */
    var isDeviceAlreadyRegistered: Boolean = false
        get() {
            return mSharePreferences.getBoolean(LocalStorageKey.IS_DEVICE_REGISTERED.name, false)
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.IS_DEVICE_REGISTERED.name, value)
        }

    /**
     * FCM Device token
     */
    var deviceToken: String = ""
        get() {
            return mSharePreferences.getString(LocalStorageKey.DEVICE_TOKEN.name)
        }
        set(value) {
            field = value
            mSharePreferences.setString(LocalStorageKey.DEVICE_TOKEN.name, value)
        }

    /**
     * User Profile
     */
/*    var userProfile: UserProfile = UserProfile()
        get() {
            return try {
                val jsonString = mSharePreferences.getString(LocalStorageKey.USER_PROFILE.name)
                Gson().fromJson<UserProfileResponse.Data>(
                    jsonString,
                    UserProfileResponse.Data::class.java
                )
            } catch (ex: Exception) {
                Timber.e(ex)
                UserProfile()
            }
        }
        set(value) {
            field = value
            val json = Gson().toJson(value)
            mSharePreferences.setString(LocalStorageKey.USER_PROFILE.name, json)
        }*/

    /**
     * flag to disable all the join ui.
     */
    var isAnyPublicOrPrivateJoined: Boolean = false
        get() {
            return mSharePreferences.getBoolean(LocalStorageKey.IS_JOINED_ANY_CHALLENGE.name, false)
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.IS_JOINED_ANY_CHALLENGE.name, value)
        }

    /**
     * joined challenge Uid.
     */
    var joinedChallengedUid: String = ""
        get() {
            return mSharePreferences.getString(LocalStorageKey.JOINED_CHALLENGE_UID.name)
        }
        set(value) {
            field = value
            mSharePreferences.setString(LocalStorageKey.JOINED_CHALLENGE_UID.name, value)
        }

    /**
     * first time visit to follow and friend.
     */
    var isFollowFirstVisit: Boolean = true
        get() {
            return mSharePreferences.getBoolean(LocalStorageKey.IS_FIRST_FOLLOW_VISIT.name, true)
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.IS_FIRST_FOLLOW_VISIT.name, value)
        }

    /**
     * Is dashboard opened for the first time.
     */
    var isDashboardOpenedFirstTime: Boolean = false
        get() {
            return mSharePreferences.getBoolean(
                LocalStorageKey.IS_DASHBOARD_OPENED_FIRST_TIME.name,
                false
            )
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.IS_DASHBOARD_OPENED_FIRST_TIME.name, value)
        }


    /**
     * AVG step of user.
     */
    var avgStepOfUser: Int = 1
        get() {
            return mSharePreferences.getInt(LocalStorageKey.AVG_STEP.name)
        }
        set(value) {
            field = value
            mSharePreferences.setInt(LocalStorageKey.AVG_STEP.name, value)
        }

    var loggedInUserStatus: Boolean = false
        get() {
            return mSharePreferences.getBoolean(LocalStorageKey.LOGGED_IN_USER_STATUS.name, false)
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.LOGGED_IN_USER_STATUS.name, value)
        }
    /**
     * Referral Code.
     */
    var referralCodeOfUser: String = ""
        get() {
            return mSharePreferences.getString(LocalStorageKey.REFERRAL_CODE_USER.name)
        }
        set(value) {
            field = value
            mSharePreferences.setString(LocalStorageKey.REFERRAL_CODE_USER.name, value)
        }

    var isFreeChallenge: Boolean = false
        get() {
            return mSharePreferences.getBoolean(LocalStorageKey.IS_FREE_CHALLENGE.name, false)
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.IS_FREE_CHALLENGE.name, value)
        }
    /**
     * information to show user
     */
    var information: String =""
        get() {
            return mSharePreferences.getString(LocalStorageKey.INFORMATION.name)
        }
        set(value) {
            field = value
            mSharePreferences.setString(LocalStorageKey.INFORMATION.name, value)
        }

    var infoChallengeClicked: Boolean = false
        get() {
            return mSharePreferences.getBoolean(LocalStorageKey.INFOCHALLENGECLICKED.name, false)
        }
        set(value) {
            field = value
            mSharePreferences.setBoolean(LocalStorageKey.INFOCHALLENGECLICKED.name, value)
        }

    fun clearSharedPref() {
        mSharePreferences.clear()
    }
}
