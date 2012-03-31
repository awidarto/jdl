package com.kickstartlab.jm;

public class DeliveryOrder {
	private String delivery_id = "";
	private String mtransaction_id = "";
	private String merchant_name = "";
	private String buyer_name = "";
	
	public void setDeliveryID(String delivery_id){
		this.delivery_id = delivery_id;
	}
	
	public String getDeliveryID(){
		return delivery_id;
	}

	public void setMtransactionID(String mtransaction_id){
		this.mtransaction_id = mtransaction_id;
	}
	
	public String getMtransactionID(){
		return mtransaction_id;
	}

	public void setMerchantName(String merchant_name){
		this.merchant_name = merchant_name;
	}
	
	public String getMerchantName(){
		return merchant_name;
	}

	public void setBuyerName(String buyer_name){
		this.buyer_name = buyer_name;
	}
	
	public String getBuyerName(){
		return buyer_name;
	}

}
