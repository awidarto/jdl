package com.kickstartlab.jm;

public class LogData {
	private long id;
	private String sync_id,capture_time,report_time,delivery_id,merchant_trans_id,status,delivery_note,latitude,longitude;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSyncId() {
		return sync_id;
	}
	public void setSyncId(String sync_id) {
		this.sync_id = sync_id;
	}
	public String getCaptureTime() {
		return capture_time;
	}
	public void setCaptureTime(String capture_time) {
		this.capture_time = capture_time;
	}
	public String getReportTime() {
		return report_time;
	}
	public void setReportTime(String report_time) {
		this.report_time = report_time;
	}
	public String getDeliveryId() {
		return delivery_id;
	}
	public void setDeliveryId(String delivery_id) {
		this.delivery_id = delivery_id;
	}
	public String getMcTransId() {
		return merchant_trans_id;
	}
	public void setMcTransId(String merchant_trans_id) {
		this.merchant_trans_id = merchant_trans_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDeliveryNote() {
		return delivery_note;
	}
	public void setDeliveryNote(String delivery_note) {
		this.delivery_note = delivery_note;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String toString(){
		return capture_time +"::"+ status; 
	}

	
}
