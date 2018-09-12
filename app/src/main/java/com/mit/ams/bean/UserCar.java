package com.mit.ams.bean;
/** 
 * description: 车辆
 * autour: BlueAmer
 * date: 17.7.21 14:28 
 * update: 17.7.21
 * version: 
*/
public class UserCar {
	private int car_id;
	private String  user_id = "";
	private String car_brand = "";
	private String car_type = "";
	private String car_plates = "";
	private String car_vin = "";
	private String add_time = "";
	private String car_item = "";
	private String vehicle_code = "";
	private String vehicle_name = "";
	private String brand_name = "";
	private String family_name = "";
	private String group_name = "";
	private String cfg_level = "";
	private String year_pattern = "";

	public int getCar_id() {
		return car_id;
	}
	public void setCar_id(int car_id) {
		this.car_id = car_id;
	}

	public String  getUser_id() {
		return user_id;
	}
	public void setUser_id(String  user_id) {
		this.user_id = user_id;
	}
	public String getCar_brand() {
		return car_brand;
	}
	public void setCar_brand(String car_brand) {
		this.car_brand = car_brand;
	}
	public String getCar_type() {
		return car_type;
	}
	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}
	public String getCar_plates() {
		return car_plates;
	}
	public void setCar_plates(String car_plates) {
		this.car_plates = car_plates;
	}
	public String getCar_vin() {
		return car_vin;
	}
	public void setCar_vin(String car_vin) {
		this.car_vin = car_vin;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getCar_item() {
		return car_item;
	}
	public void setCar_item(String car_item) {
		this.car_item = car_item;
	}
	public String getVehicle_code() {
		return vehicle_code;
	}
	public void setVehicle_code(String vehicle_code) {
		this.vehicle_code = vehicle_code;
	}
	public String getVehicle_name() {
		return vehicle_name;
	}
	public void setVehicle_name(String vehicle_name) {
		this.vehicle_name = vehicle_name;
	}
	public String getBrand_name() {
		return brand_name;
	}
	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}
	public String getFamily_name() {
		return family_name;
	}
	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getCfg_level() {
		return cfg_level;
	}
	public void setCfg_level(String cfg_level) {
		this.cfg_level = cfg_level;
	}
	public String getYear_pattern() {
		return year_pattern;
	}
	public void setYear_pattern(String year_pattern) {
		this.year_pattern = year_pattern;
	}

	@Override
	public String toString() {
		return this.car_plates;
	}
}
