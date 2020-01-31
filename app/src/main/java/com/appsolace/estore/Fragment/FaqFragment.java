package com.appsolace.estore.Fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.appsolace.estore.Adapter.FaqAdapter;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.Model.FaqData;
import com.appsolace.estore.R;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class FaqFragment extends Fragment {

    SharedPreferences userPref;

    Dialog ProgressDialog;
    ProgressBar progressBar;


    @BindView(R.id.recycler_testimonial)
    RecyclerView recycler_testimonial;

    List<FaqData> faqDataList;

    FaqAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.testimonial_fragment, container, false);
        ButterKnife.bind(this, view);

        initdata();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Common.isNetworkAvailable(getActivity())) {
            requestHomeCategory();
        } else {

            RetriveCall();
        }

    }

    private void RetriveCall() {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(getActivity()).buildDialogInternate(new CallbackInternate() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();

                if (Common.isNetworkAvailable(getActivity())) {
                    requestHomeCategory();
                } else {
                    RetriveCall();
                }
            }
        });
        dialog.show();
    }

    public void initdata() {

        userPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_testimonial.setLayoutManager(mLayoutManager);
        recycler_testimonial.setHasFixedSize(true);


        ProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.progressbar);
        ProgressDialog.setCancelable(false);
        progressBar = (ProgressBar) ProgressDialog.findViewById(R.id.progress_circular);

    }

    private void requestHomeCategory() {

        ProgressDialog.show();

        API api = RestAdapter.createAPI();
        Call<JsonObject> callback_testimonial = api.faq(userPref.getString("uid", ""));
        callback_testimonial.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("HomeCategory onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();


                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        JsonArray faq_detail;

                        if (jsonObject.has("faq_detail") && jsonObject.get("faq_detail").isJsonArray())

                            faq_detail = jsonObject.get("faq_detail").getAsJsonArray();
                        else
                            faq_detail = null;


                        faqDataList = new ArrayList<>();

                        if (faq_detail != null && faq_detail.size() > 0) {


                            for (int i = 0; i < faq_detail.size(); i++) {

                                JsonObject jsonObject_testimonial = faq_detail.get(i).getAsJsonObject();

                                FaqData testimonialData = new FaqData();

                                if (jsonObject_testimonial.has("faq_id") && !jsonObject_testimonial.get("faq_id").isJsonNull())
                                    testimonialData.setFaq_id(jsonObject_testimonial.get("faq_id").getAsString());
                                else
                                    testimonialData.setFaq_id("");

                                if (jsonObject_testimonial.has("question") && !jsonObject_testimonial.get("question").isJsonNull())
                                    testimonialData.setQuestion(jsonObject_testimonial.get("question").getAsString());
                                else
                                    testimonialData.setQuestion("");

                                if (jsonObject_testimonial.has("description") && !jsonObject_testimonial.get("description").isJsonNull())
                                    testimonialData.setDescription(jsonObject_testimonial.get("description").getAsString());
                                else
                                    testimonialData.setDescription("");



                                faqDataList.add(testimonialData);
                            }


                            recycler_testimonial.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new FaqAdapter(getActivity(),faqDataList);
                            recycler_testimonial.setAdapter(adapter);

                        }


                    } else {
                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        int Isactive=0;
                        if (jsonObject.has("Isactive") && !jsonObject.get("Isactive").isJsonNull()) {
                            Isactive = jsonObject.get("Isactive").getAsInt();
                        }

                        if (Isactive == 0) {
                            Common.AccountLock(Common.Activity);
                        }
                        else if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                            ErrorMessage(jsonObject.get("message").toString());
                        }
                    }

                } else {

                    if (ProgressDialog != null && ProgressDialog.isShowing())
                        ProgressDialog.dismiss();

                    ErrorMessage(getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                if (ProgressDialog != null && ProgressDialog.isShowing())
                    ProgressDialog.dismiss();

                String message = "";
                if (t instanceof SocketTimeoutException) {
                    message = "Socket Time out. Please try again.";
                    Common.ShowHttpErrorMessage(getActivity(), message);
                }
            }
        });

    }

    private void ErrorMessage(String Message) {
        // Common.showInternetInfo(ActivitySignIn.this, "Network is not available");
        Dialog dialog = new DialogUtils(getActivity()).buildDialogMessage(new CallbackMessage() {
            @Override
            public void onSuccess(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }
        }, Message);
        dialog.show();
    }
}
