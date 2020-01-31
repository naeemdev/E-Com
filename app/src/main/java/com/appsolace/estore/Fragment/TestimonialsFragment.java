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
import com.appsolace.estore.Adapter.TestimonialAdapter;
import com.appsolace.estore.Model.TestimonialData;
import com.appsolace.estore.Utili.Common;
import com.appsolace.estore.View.DialogUtils;
import com.appsolace.estore.callback.CallbackInternate;
import com.appsolace.estore.connection.API;
import com.appsolace.estore.callback.CallbackMessage;
import com.appsolace.estore.R;
import com.appsolace.estore.connection.RestAdapter;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TestimonialsFragment extends Fragment {

    SharedPreferences userPref;

    Dialog ProgressDialog;
    ProgressBar progressBar;


    @BindView(R.id.recycler_testimonial)
    RecyclerView recycler_testimonial;

    List<TestimonialData> testimonialDataList;

    TestimonialAdapter adapter;

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
        Call<JsonObject> callback_testimonial = api.testimonial(userPref.getString("uid", ""));
        callback_testimonial.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("HomeCategory onResponse", "=>" + response.body());

                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();


                    if (jsonObject != null && jsonObject.get("status").getAsString().equals("success")) {

                        if (ProgressDialog != null && ProgressDialog.isShowing())
                            ProgressDialog.dismiss();

                        JsonArray testimonial_detail;

                        if (jsonObject.has("testimonial_detail") && jsonObject.get("testimonial_detail").isJsonArray())

                            testimonial_detail = jsonObject.get("testimonial_detail").getAsJsonArray();
                        else
                            testimonial_detail = null;


                        testimonialDataList = new ArrayList<>();

                        if (testimonial_detail != null && testimonial_detail.size() > 0) {


                            for (int i = 0; i < testimonial_detail.size(); i++) {

                                JsonObject jsonObject_testimonial = testimonial_detail.get(i).getAsJsonObject();

                                TestimonialData testimonialData = new TestimonialData();

                                if (jsonObject_testimonial.has("testimonial_id") && !jsonObject_testimonial.get("testimonial_id").isJsonNull())
                                    testimonialData.setTestimonial_id(jsonObject_testimonial.get("testimonial_id").getAsString());
                                else
                                    testimonialData.setTestimonial_id("");

                                if (jsonObject_testimonial.has("name") && !jsonObject_testimonial.get("name").isJsonNull())
                                    testimonialData.setName(jsonObject_testimonial.get("name").getAsString());
                                else
                                    testimonialData.setName("");

                                if (jsonObject_testimonial.has("description") && !jsonObject_testimonial.get("description").isJsonNull())
                                    testimonialData.setDescription(jsonObject_testimonial.get("description").getAsString());
                                else
                                    testimonialData.setDescription("");

                                if (jsonObject_testimonial.has("image") && !jsonObject_testimonial.get("image").isJsonNull())
                                    testimonialData.setImage(jsonObject_testimonial.get("image").getAsString());
                                else
                                    testimonialData.setImage("");

                                testimonialDataList.add(testimonialData);
                            }


                            recycler_testimonial.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new TestimonialAdapter(getActivity(), testimonialDataList);
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
