package com.aiosleeve.aiosleeve.interfaces;


import com.aiosleeve.aiosleeve.VO.VOBPMTableData;
import com.aiosleeve.aiosleeve.VO.VOGetUserData;
import com.aiosleeve.aiosleeve.VO.VOLogOut;
import com.aiosleeve.aiosleeve.VO.VOLogin;
import com.aiosleeve.aiosleeve.VO.VOResponseForgotPassword;
import com.aiosleeve.aiosleeve.VO.VOSignUp;
import com.aiosleeve.aiosleeve.VO.VOSleepSyncDataToServer;
import com.aiosleeve.aiosleeve.VO.VOUpdateUserProfile;
import com.aiosleeve.aiosleeve.VO.VoGetMedication;
import com.aiosleeve.aiosleeve.VO.VoMedicationTaken;
import com.aiosleeve.aiosleeve.VO.VoResponseBPMSync;
import com.aiosleeve.aiosleeve.VORequest.VoRequestBPMDetails;
import com.aiosleeve.aiosleeve.VORequest.VoRequestBPMItem;
import com.aiosleeve.aiosleeve.VORequest.VoRequestSleepItem;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by oneclickpc001 on 1/2/17.
 */

public interface API {

    @FormUrlEncoded
    @POST("normalUserLogin")
    Call<VOLogin> normalUserLogin(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("forgotPassword")
    Call<VOResponseForgotPassword> forgotPassword(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("normalUserLogut")
    Call<VOLogOut> normalUserLogut(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("normalUserSignup")
    Call<VOSignUp> normalUserSignup(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("getUserDataByUserId")
    Call<VOGetUserData> getUserDataByUserId(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("updateUserProfile")
    Call<VOUpdateUserProfile> updateUserProfile(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("bpmActivityTable")
    Call<VOBPMTableData> bpmActivityTable(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("syncSleepDataToServer")
    Call<VOSleepSyncDataToServer> syncSleepDataToServer(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("getMedactions")
    Call<VoGetMedication> getMedactions(@FieldMap Map<String, String> mHashMap);

    @POST("syncBPMActivityToServer")
    Call<VoResponseBPMSync> syncBPMDataToServer(@Body VoRequestBPMItem mVoRequestBPMItem);

    @FormUrlEncoded
    @POST("setMedactions")
    Call<VoMedicationTaken> syncMadicationTakenDataToServer(@FieldMap Map<String, String> mHashMap);

    @FormUrlEncoded
    @POST("setMedication")
    Call<VOLogOut> setMedication(@FieldMap Map<String, String> mHashMap);

}