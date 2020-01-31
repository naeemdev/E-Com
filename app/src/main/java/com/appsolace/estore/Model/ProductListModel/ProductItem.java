package com.appsolace.estore.Model.ProductListModel;

import java.io.Serializable;
import java.util.List;

public class ProductItem implements Serializable {

	/* Product Details */
	private List<ProductImageItem> productImage;
	private List<ProductAttributeItem> productAttribute;
	private String productStatus;
	private List<ProductSizeItem> productSize;
	private String productColor;
	private String productPrice;
	private String productName;
	private String productPrimaryImage;
	private String isProductCustomizable;
	private String productId;
	private String productOldPrice;
	private String productStock;
	private String productBrand;
	private String productDescription;
	private int favoriteFlag;

	/* Order History */
	private String comment;
	private String colorName;
	private String sizeId;
	private String sizeName;
	private String priceItem;
	private String price;
	private String colorCode;
	private String amount;
	private String quantity;
	private String bookStatus;
	private String orderImage;

	/* Order shopingcart */
	private String order_id;
	private String current_stock;
	private String user_id;
	private String user_name;
	private String address1;
	private String address2;
	private String pincode;
	private String city;
	private String state;
	private String mobile;
	private String isFormSelected;
	private String stock_flag;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getCurrent_stock() {
		return current_stock;
	}

	public void setCurrent_stock(String current_stock) {
		this.current_stock = current_stock;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIsFormSelected() {
		return isFormSelected;
	}

	public void setIsFormSelected(String isFormSelected) {
		this.isFormSelected = isFormSelected;
	}

	public String getStock_flag() {
		return stock_flag;
	}

	public void setStock_flag(String stock_flag) {
		this.stock_flag = stock_flag;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getSizeId() {
		return sizeId;
	}

	public void setSizeId(String sizeId) {
		this.sizeId = sizeId;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public String getPriceItem() {
		return priceItem;
	}

	public void setPriceItem(String priceItem) {
		this.priceItem = priceItem;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getBookStatus() {
		return bookStatus;
	}

	public void setBookStatus(String bookStatus) {
		this.bookStatus = bookStatus;
	}

	public String getOrderImage() {
		return orderImage;
	}

	public void setOrderImage(String orderImage) {
		this.orderImage = orderImage;
	}

	public void setProductImage(List<ProductImageItem> productImage){
		this.productImage = productImage;
	}

	public List<ProductImageItem> getProductImage(){
		return productImage;
	}

	public void setProductAttribute(List<ProductAttributeItem> productAttribute){
		this.productAttribute = productAttribute;
	}

	public List<ProductAttributeItem> getProductAttribute(){
		return productAttribute;
	}

	public void setProductStatus(String productStatus){
		this.productStatus = productStatus;
	}

	public String getProductStatus(){
		return productStatus;
	}

	public void setProductSize(List<ProductSizeItem> productSize){
		this.productSize = productSize;
	}

	public List<ProductSizeItem> getProductSize(){
		return productSize;
	}

	public void setProductColor(String productColor){
		this.productColor = productColor;
	}

	public String getProductColor(){
		return productColor;
	}

	public void setProductPrice(String productPrice){
		this.productPrice = productPrice;
	}

	public String getProductPrice(){
		return productPrice;
	}

	public void setProductName(String productName){
		this.productName = productName;
	}

	public String getProductName(){
		return productName;
	}

	public void setProductPrimaryImage(String productPrimaryImage){
		this.productPrimaryImage = productPrimaryImage;
	}

	public String getProductPrimaryImage(){
		return productPrimaryImage;
	}

	public void setIsProductCustomizable(String isProductCustomizable){
		this.isProductCustomizable = isProductCustomizable;
	}

	public String getIsProductCustomizable(){
		return isProductCustomizable;
	}

	public void setProductId(String productId){
		this.productId = productId;
	}

	public String getProductId(){
		return productId;
	}

	public void setProductOldPrice(String productOldPrice){
		this.productOldPrice = productOldPrice;
	}

	public String getProductOldPrice(){
		return productOldPrice;
	}

	public void setProductStock(String productStock){
		this.productStock = productStock;
	}

	public String getProductStock(){
		return productStock;
	}

	public void setProductBrand(String productBrand){
		this.productBrand = productBrand;
	}

	public String getProductBrand(){
		return productBrand;
	}

	public void setProductDescription(String productDescription){
		this.productDescription = productDescription;
	}

	public String getProductDescription(){
		return productDescription;
	}

	public void setFavoriteFlag(int favoriteFlag){
		this.favoriteFlag = favoriteFlag;
	}

	public int getFavoriteFlag(){
		return favoriteFlag;
	}


}