/**    
* @Title: RepairFactory.java  
* @Package com.ds.mybatis.dsws.entity  
* @Description: TODO(用一句话描述该文件做什么)  
* @author 刘鹏飞
* @date 2017-7-18 下午3:08:59  
* @version V1.0    
*/
package com.mit.ams.bean;

/**  
 * @ClassName: RepairFactory  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 刘鹏飞
 * @date 2017-7-18 下午3:08:59  
 *    
 */
public class RepairFactory {

	private String id;
	private String city;
	private String address;
	private String repair_fty_name;
	private String lon;
	private String lat;
	private String mobile;
	private String photo;
	private String fty_photo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRepair_fty_name() {
		return repair_fty_name;
	}
	public void setRepair_fty_name(String repair_fty_name) {
		this.repair_fty_name = repair_fty_name;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getFty_photo() {
		return fty_photo;
	}

	public void setFty_photo(String fty_photo) {
		this.fty_photo = fty_photo;
	}
}
