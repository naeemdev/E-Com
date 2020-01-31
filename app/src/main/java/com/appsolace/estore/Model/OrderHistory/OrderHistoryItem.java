package com.appsolace.estore.Model.OrderHistory;

import com.appsolace.estore.Model.ProductListModel.ProductItem;

import java.io.Serializable;
import java.util.List;

public class OrderHistoryItem implements Serializable{
	private String pincode;
	private String bookStatus;
	private String totalPrice;
	private String address2;
	private String city;
	private String userName;
	private String address1;
	private List<ProductItem> productDetail;
	private String mobile;
	private String taxPrice;
	private String tax;
	private List<String> placedProductImage;
	private String addressName;
	private String orderDate;
	private String paymentType;
	private String userId;
	private String cancelDate;
	private String totalQuantity;
	private String state;
	private String orderId;

	public void setPincode(String pincode){
		this.pincode = pincode;
	}

	public String getPincode(){
		return pincode;
	}

	public void setBookStatus(String bookStatus){
		this.bookStatus = bookStatus;
	}

	public String getBookStatus(){
		return bookStatus;
	}

	public void setTotalPrice(String totalPrice){
		this.totalPrice = totalPrice;
	}

	public String getTotalPrice(){
		return totalPrice;
	}

	public void setAddress2(String address2){
		this.address2 = address2;
	}

	public String getAddress2(){
		return address2;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setAddress1(String address1){
		this.address1 = address1;
	}

	public String getAddress1(){
		return address1;
	}

	public void setProductDetail(List<ProductItem> productDetail){
		this.productDetail = productDetail;
	}

	public List<ProductItem> getProductDetail(){
		return productDetail;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setTaxPrice(String taxPrice){
		this.taxPrice = taxPrice;
	}

	public String getTaxPrice(){
		return taxPrice;
	}

	public void setTax(String tax){
		this.tax = tax;
	}

	public String getTax(){
		return tax;
	}

	public void setPlacedProductImage(List<String> placedProductImage){
		this.placedProductImage = placedProductImage;
	}

	public List<String> getPlacedProductImage(){
		return placedProductImage;
	}

	public void setAddressName(String addressName){
		this.addressName = addressName;
	}

	public String getAddressName(){
		return addressName;
	}

	public void setOrderDate(String orderDate){
		this.orderDate = orderDate;
	}

	public String getOrderDate(){
		return orderDate;
	}

	public void setPaymentType(String paymentType){
		this.paymentType = paymentType;
	}

	public String getPaymentType(){
		return paymentType;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setCancelDate(String cancelDate){
		this.cancelDate = cancelDate;
	}

	public String getCancelDate(){
		return cancelDate;
	}

	public void setTotalQuantity(String totalQuantity){
		this.totalQuantity = totalQuantity;
	}

	public String getTotalQuantity(){
		return totalQuantity;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setOrderId(String orderId){
		this.orderId = orderId;
	}

	public String getOrderId(){
		return orderId;
	}

}