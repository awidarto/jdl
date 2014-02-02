package com.kickstartlab.jm;

public class Order {
	private long id;
	private int seq;
	private String delivery_id,mc_name,mc_trans_id,ship_addr,ship_dir,by_name,by_phone,rec_name,tot_price,delivery_cost,cod_cost,cod_curr,dl_type,dl_status,as_zone,as_city,by_zone, by_city;
		
	/**
	 * @return the as_zone
	 */
	public String getAs_zone() {
		return as_zone;
	}
	/**
	 * @param as_zone the as_zone to set
	 */
	public void setAs_zone(String as_zone) {
		this.as_zone = as_zone;
	}
	/**
	 * @return the as_city
	 */
	public String getAs_city() {
		return as_city;
	}
	/**
	 * @param as_city the as_city to set
	 */
	public void setAs_city(String as_city) {
		this.as_city = as_city;
	}
	/**
	 * @return the by_zone
	 */
	public String getBy_zone() {
		return by_zone;
	}
	/**
	 * @param by_zone the by_zone to set
	 */
	public void setBy_zone(String by_zone) {
		this.by_zone = by_zone;
	}
	/**
	 * @return the by_city
	 */
	public String getBy_city() {
		return by_city;
	}
	/**
	 * @param by_city the by_city to set
	 */
	public void setBy_city(String by_city) {
		this.by_city = by_city;
	}
	/**
	 * @return the tot_price
	 */
	public String getTot_price() {
		return tot_price;
	}
	/**
	 * @param tot_price the tot_price to set
	 */
	public void setTot_price(String tot_price) {
		this.tot_price = tot_price;
	}
	/**
	 * @return the delivery_cost
	 */
	public String getDelivery_cost() {
		return delivery_cost;
	}
	/**
	 * @param delivery_cost the delivery_cost to set
	 */
	public void setDelivery_cost(String delivery_cost) {
		this.delivery_cost = delivery_cost;
	}
	/**
	 * @return the dl_type
	 */
	public String getDl_type() {
		return dl_type;
	}
	/**
	 * @param dl_type the dl_type to set
	 */
	public void setDl_type(String dl_type) {
		this.dl_type = dl_type;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
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

	public String getDirection() {
		return ship_dir;
	}
	
	public void setDirection(String ship_dir) {
		this.ship_dir = ship_dir;
	}
	
	@Override
	public String toString(){
		return delivery_id;
	}

}
