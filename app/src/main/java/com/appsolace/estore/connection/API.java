package com.appsolace.estore.connection;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface API {

  String TYPE = "content-type: application/x-www-form-urlencoded";
  //String CACHE = "cache-control: no-cache";
  String CACHE = "Cache-Control: no-cache";
  String AGENT = "User-Agent: 4e6e931e-f7f8-8924-4587-a553e42d1f61";

  @FormUrlEncoded
  @POST("signup_user")
  Call<JsonObject> Register(
          @Field("user_id") String user_id,
          @Field("name") String name,
          @Field("email") String email,
          @Field("password") String password,
          @Field("facebook_id") String facebook_id,
          @Field("subscribe_offer") String subscribe_offer,
          @Field("isdevice") String isdevice

  );

  @FormUrlEncoded
  @POST("gest_login")
  Call<JsonObject> GuestLogin(
          @Field("user_id") String uid,
          @Field("token") String token,
          @Field("is_device") int idevice
  );


  @FormUrlEncoded
  @POST("get_login")
  Call<JsonObject> Login(
          @Field("email") String name,
          @Field("password") String password
  );

  @FormUrlEncoded
  @POST("social_login")
  Call<JsonObject> social_login(
          @Field("facebook_id") String facebook_id
  );

  @FormUrlEncoded
  @POST("home_category")
  Call<JsonObject> HomeCategory(
          @Field("user_id") String uid
  );


  @FormUrlEncoded
  @POST("testimonial")
  Call<JsonObject> testimonial(
          @Field("user_id") String uid
  );

  @FormUrlEncoded
  @POST("faq")
  Call<JsonObject> faq(
          @Field("user_id") String uid
  );

  @FormUrlEncoded
  @POST("category_list")
  Call<JsonObject> category_list(
          @Field("user_id") String uid,
          @Field("category_id") String category_id,
          @Field("off") String off
  );

  @FormUrlEncoded
  @POST("product_list")
  Call<JsonObject> product_list(
          @Field("user_id") String uid,
          @Field("category_id") String category_id,
          @Field("off") String off
  );



  @FormUrlEncoded
  @POST("short_product")
  Call<JsonObject> short_product(
          @Field("user_id") String uid,
          @Field("category_id") String category_id,
          @Field("flag") String flag,
          @Field("off") String off
  );

  @FormUrlEncoded
  @POST("product_filter")
  Call<JsonObject> product_filter(
          @Field("user_id") String uid,
          @Field("category_id") String category_id,
          @Field("tag") String tag,
          @Field("size") String size,
          @Field("brand") String brand,
          @Field("color") String color,
          @Field("start_price") String start_price,
          @Field("end_price") String end_price,
          @Field("flag") String flag,
          @Field("off") String off
  );

  @FormUrlEncoded
  @POST("product_detail")
  Call<JsonObject> product_detail(
          @Field("user_id") String uid,
          @Field("product_id") String category_id
  );


  @FormUrlEncoded
  @POST("user_view_product")
  Call<JsonObject> user_view_product(
          @Field("user_id") String uid,
          @Field("product_id") String category_id
  );

  @FormUrlEncoded
  @POST("product_favorite")
  Call<JsonObject> product_favorite(
          @Field("user_id") String uid,
          @Field("product_id") String category_id
  );

  @FormUrlEncoded
  @POST("user_favorite")
  Call<JsonObject> user_favorite(
          @Field("user_id") String uid,
          @Field("product_id") String category_id
  );

  @FormUrlEncoded
  @POST("remove_user_favorite")
  Call<JsonObject> remove_user_favorite(
          @Field("user_id") String uid,
          @Field("product_id") String category_id
  );

  @FormUrlEncoded
  @POST("user_favorite_list")
  Call<JsonObject> user_favorite_list(
          @Field("user_id") String uid,
          @Field("off") String off
  );

  @FormUrlEncoded
  @POST("edit_profile")
  Call<JsonObject> edit_profile(
          @Field("user_id") String uid,
          @Field("name") String name,
          @Field("isdevice") String isdevice
  );

  @FormUrlEncoded
  @POST("change_password")
  Call<JsonObject> change_password(
          @Field("user_id") String uid,
          @Field("password") String password,
          @Field("isdevice") String isdevice
  );

  @Headers({CACHE, TYPE})
  @FormUrlEncoded
  @POST("Privacy_Policy")
  Call<JsonObject> privacyPolicy(
          @Field("user_id") String usrId
  );

  @Headers({CACHE, TYPE})
  @FormUrlEncoded
  @POST("term_condition")
  Call<JsonObject> termCondition(
          @Field("user_id") String usrId
  );

  @Headers({CACHE, TYPE})
  @FormUrlEncoded
  @POST("cancel_and_return")
  Call<JsonObject> cancelAndReturn(
          @Field("user_id") String usrId
  );

  @Headers({CACHE, TYPE})
  @FormUrlEncoded
  @POST("secure_payment")
  Call<JsonObject> securePayment(
          @Field("user_id") String usrId
  );

  @Headers({CACHE, TYPE})
  @FormUrlEncoded
  @POST("delivery")
  Call<JsonObject> delivery(
          @Field("user_id") String usrId
  );

  @Headers({CACHE, TYPE})
  @FormUrlEncoded
  @POST("about_page")
  Call<JsonObject> aboutPage(
          @Field("user_id") String usrId
  );

  @FormUrlEncoded
  @POST("order_history")
  Call<JsonObject> order_history(
          @Field("user_id") String uid
  );

  @FormUrlEncoded
  @POST("confirm_order_history")
  Call<JsonObject> confirm_order_history(
          @Field("user_id") String uid,
          @Field("off") String off
  );

  @FormUrlEncoded
  @POST("stock_check")
  Call<JsonObject> stock_check(
          @Field("user_id") String uid,
          @Field("product_id") String product_id,
          @Field("order_id") String order_id,
          @Field("quantity") String quantity,
          @Field("size_id") String size_id,
          @Field("color") String color
  );



  @FormUrlEncoded
  @POST("remove_order")
  Call<JsonObject> remove_order(
          @Field("user_id") String uid,
          @Field("order_id") String order_id
  );

  @FormUrlEncoded
  @POST("filter_tag")
  Call<JsonObject> filter_tag(
          @Field("user_id") String uid,
          @Field("category_id") String category_id
  );

  @FormUrlEncoded
  @POST("filter_brand")
  Call<JsonObject> filter_brand(
          @Field("user_id") String uid,
          @Field("category_id") String category_id
  );

  @FormUrlEncoded
  @POST("filter_price")
  Call<JsonObject> filter_price(
          @Field("user_id") String uid,
          @Field("category_id") String category_id
  );

  @FormUrlEncoded
  @POST("filter_size")
  Call<JsonObject> filter_size(
          @Field("user_id") String uid,
          @Field("category_id") String category_id
  );

  @FormUrlEncoded
  @POST("filter_color")
  Call<JsonObject> filter_color(
          @Field("user_id") String uid
  );

  @FormUrlEncoded
  @POST("cart_stock_check")
  Call<JsonObject> cart_stock_check(
          @Field("user_id") String uid,
          @Field("product_id") String product_id,
          @Field("size_id") String size_id,
          @Field("color") String color
  );


  @Headers({"Content-Type: application/json"})
  @POST("submitProductOrder")
  Call<JsonObject> submitProductOrder(
          @Body JsonObject jsonObject
  );


  @FormUrlEncoded
  @POST("address_list")
  Call<JsonObject> address_list(
          @Field("user_id") String uid
  );

  @FormUrlEncoded
  @POST("remove_address")
  Call<JsonObject> remove_address(
          @Field("user_id") String uid,
          @Field("address_id") String address_id
  );

  @FormUrlEncoded
  @POST("add_address")
  Call<JsonObject> add_address(
          @Field("user_id") String uid,
          @Field("name") String name,
          @Field("address") String address,
          @Field("pincode") String pincode,
          @Field("city") String city,
          @Field("state") String state,
          @Field("mobile_no") String mobile_no
  );

  @FormUrlEncoded
  @POST("edit_address")
  Call<JsonObject> edit_address(
          @Field("user_id") String uid,
          @Field("name") String name,
          @Field("address") String address,
          @Field("pincode") String pincode,
          @Field("city") String city,
          @Field("state") String state,
          @Field("mobile_no") String mobile_no,
          @Field("address_id") String address_id
  );

  @Headers({"Content-Type: application/json"})
  @POST("checkout_ProductOrder")
  Call<JsonObject> checkout_ProductOrder(
          @Body JsonObject jsonObject
  );


  @FormUrlEncoded
  @POST("cancel_confirm_order")
  Call<JsonObject> cancel_confirm_order(
          @Field("user_id") String user_id,
          @Field("order_id") String order_id
  );

}
