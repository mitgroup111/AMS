package com.mit.ams.bean;

import com.mit.ams.common.StringUtils;

import java.io.Serializable;

public class CarDamageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int damage_info_id;
    private String user_id;
    private String real_name;
    private String mobile;
    private String car_brand;
    private String car_type;
    private String car_item;
    private String damage_info;
    private String add_time;
    private String assess_result;
    private String lat;
    private String lon;
    private String damage_des;
    private String flag;
    private String status;
    private String assess_price;
    private String vehicle_code;
    private String vehicle_name;
    private String car_id;
    
	public int getDamage_info_id() {
        return damage_info_id;
	}
	public void setDamage_info_id(int damage_info_id) {
		this.damage_info_id = damage_info_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getDamage_info() {
		return damage_info;
	}
	public void setDamage_info(String damage_info) {
		this.damage_info = damage_info;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getAssess_result() {
		return assess_result;
	}
	public void setAssess_result(String assess_result) {
		this.assess_result = assess_result;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getDamage_des() {
		return damage_des;
	}
	public void setDamage_des(String damage_des) {
		this.damage_des = damage_des;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAssess_price() {
		return assess_price;
	}
	public void setAssess_price(String assess_price) {
		this.assess_price = assess_price;
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
	public String getCar_id() {
		return car_id;
	}
	public void setCar_id(String car_id) {
		this.car_id = car_id;
	}

	@Override
	public String toString() {
		return damage_info;
	}
}