package com.kickstartlab.jm;

public class Order {
	private long id;
	private String delivery_id,mc_name,mc_trans_id,ship_addr,by_name,by_phone,rec_name,cod_cost,cod_curr,dl_status;
		
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getShipAddr() {
		return ship_addr;
	}
	public void setShipAddr(String ship_addr) {
		this.ship_addr = ship_addr;
	}
	public String getMcTransId() {
		return mc_trans_id;
	}
	public void setMcTransId(String mc_trans_id) {
		this.mc_trans_id = mc_trans_id;
	}
	public String getMcName() {
		return mc_name;
	}
	public void setMcName(String mc_name) {
		this.mc_name = mc_name;
	}
	public String getDeliveryId() {
		return delivery_id;
	}
	
	public void setDeliveryId(String delivery_id) {
		this.delivery_id = delivery_id;
	}

	public String getByName() {
		return by_name;
	}
	public void setByName(String by_name) {
		this.by_name = by_name;
	}
	
	public String getByPhone() {
		return by_phone;
	}
	
	public void setByPhone(String by_phone) {
		this.by_phone = by_phone;
	}
	
	public String getRecipient() {
		return rec_name;
	}
	
	public void setRecipient(String rec_name) {
		this.rec_name = rec_name;
	}
	
	public String getCODCost() {
		return cod_cost;
	}
	
	public void setCODCost(String cod_cost) {
		this.cod_cost = cod_cost;
	}

	public String getCODCurr() {
		return cod_curr;
	}
	
	public void setCODCurr(String cod_curr) {
		this.cod_curr = cod_curr;
	}

	public String getStatus() {
		return dl_status;
	}
	
	public void setStatus(String dl_status) {
		this.dl_status = dl_status;
	}

	@Override
	public String toString(){
		return delivery_id;
	}

}
