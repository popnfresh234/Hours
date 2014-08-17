package com.alex.hours;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

	public ParseUser getAuthor() {
		return getParseUser(ParseConstants.KEY_AUTHOR);
	}

	public void setAuthor() {
		put(ParseConstants.KEY_AUTHOR, ParseUser.getCurrentUser());
	}

	public Date getCreatedAt() {
		return getDate("createdAt");
	}
	
	public String getTitle() {
		return getString(ParseConstants.KEY_RESTAURANT_TITLE);
	}

	public void setTitle(String title) {
		put(ParseConstants.KEY_RESTAURANT_TITLE, title);
	}
	
	public String getLowerCaseTitle(){
		return getString(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE);
	}
	
	public void setLowerCaseTitle(String lowerCaseTitle){
		put(ParseConstants.KEY_RESTAURANT_LOWERCASE_TITLE, lowerCaseTitle);
	}
	
	public String getAddress(){
		return getString(ParseConstants.KEY_ADDRESS);
	}
	
	public void setAddress(String address) {
		put(ParseConstants.KEY_ADDRESS, address);
	}
	
	public String getLowerCaseAddress(){
		return getString(ParseConstants.KEY_ADDRESS_LOWER_CASE);
	}
	
	public void setLowerCaseAddress(String address) {
		put(ParseConstants.KEY_ADDRESS_LOWER_CASE, address);
	}
	
	public String getPhone(){
		return getString(ParseConstants.KEY_PHONE);
	}
	
	public void setPhone(String phone){
		put(ParseConstants.KEY_PHONE, phone);
	}
	
	public Boolean getSunday(){
		return getBoolean(ParseConstants.KEY_SUNDAY);
	}
	
	public void setSunday(Boolean var){
		put(ParseConstants.KEY_SUNDAY, var);
	}
	
	public Boolean getMonday(){
		return getBoolean(ParseConstants.KEY_MONDAY);
	}
	
	public void setMonday(Boolean var){
		put(ParseConstants.KEY_MONDAY, var);
	}
	
	public Boolean getTuesday(){
		return getBoolean(ParseConstants.KEY_TUESDAY);
	}
	
	public void setTuesday(Boolean var){
		put(ParseConstants.KEY_TUESDAY, var);
	}
	
	public Boolean getWednesday(){
		return getBoolean(ParseConstants.KEY_WEDNESDAY);
	}
	
	public void setWednesday(Boolean var){
		put(ParseConstants.KEY_WEDNESDAY, var);
	}
	
	public Boolean getThursday(){
		return getBoolean(ParseConstants.KEY_THURSDAY);
	}
	
	public void setThursday(Boolean var){
		put(ParseConstants.KEY_THURSDAY, var);
	}
	
	public Boolean getFriday(){
		return getBoolean(ParseConstants.KEY_FRIDAY);
	}
	
	public void setFriday(Boolean var){
		put(ParseConstants.KEY_FRIDAY, var);
	}
	
	public Boolean getSaturday(){
		return getBoolean(ParseConstants.KEY_SATURDAY);
	}
	
	public void setSaturday(Boolean var){
		put(ParseConstants.KEY_SATURDAY, var);
	}
	
	public String getSundayOpenHours() {
		return getString(ParseConstants.KEY_SUNDAY_OPEN_HOURS);
	}

	public void setSundayOpenHours(String open) {
		put(ParseConstants.KEY_SUNDAY_OPEN_HOURS, open);
	}
	
	public String getSundayCloseHours() {
		return getString(ParseConstants.KEY_SUNDAY_CLOSE_HOURS);
	}

	public void setSundayCloseHours(String close) {
		put(ParseConstants.KEY_SUNDAY_CLOSE_HOURS, close);
	}
	
	public String getMondayOpenHours() {
		return getString(ParseConstants.KEY_MONDAY_OPEN_HOURS);
	}

	public void setMondayOpenHours(String open) {
		put(ParseConstants.KEY_MONDAY_OPEN_HOURS, open);
	}
	
	public String getMondayCloseHours() {
		return getString(ParseConstants.KEY_MONDAY_CLOSE_HOURS);
	}

	public void setMondayCloseHours(String close) {
		put(ParseConstants.KEY_MONDAY_CLOSE_HOURS, close);
	}
	
	public String getTuesdayOpenHours() {
		return getString(ParseConstants.KEY_TUESDAY_OPEN_HOURS);
	}

	public void setTuesdayOpenHours(String open) {
		put(ParseConstants.KEY_TUESDAY_OPEN_HOURS, open);
	}
	
	public String getTuesdayCloseHours() {
		return getString(ParseConstants.KEY_TUESDAY_CLOSE_HOURS);
	}

	public void setTuesdayCloseHours(String close) {
		put(ParseConstants.KEY_TUESDAY_CLOSE_HOURS, close);
	}
	
	public String getWednesdayOpenHours() {
		return getString(ParseConstants.KEY_WEDNESDAY_OPEN_HOURS);
	}

	public void setWednesdayOpenHours(String open) {
		put(ParseConstants.KEY_WEDNESDAY_OPEN_HOURS, open);
	}
	
	public String getWednesdayCloseHours() {
		return getString(ParseConstants.KEY_WEDNESDAY_CLOSE_HOURS);
	}

	public void setWednesdayCloseHours(String close) {
		put(ParseConstants.KEY_WEDNESDAY_CLOSE_HOURS, close);
	}
	
	public String getThursdayOpenHours() {
		return getString(ParseConstants.KEY_THURSDAY_OPEN_HOURS);
	}

	public void setThursdayOpenHours(String open) {
		put(ParseConstants.KEY_THURSDAY_OPEN_HOURS, open);
	}
	
	public String getThursdayCloseHours() {
		return getString(ParseConstants.KEY_THURSDAY_CLOSE_HOURS);
	}

	public void setThursdayCloseHours(String close) {
		put(ParseConstants.KEY_THURSDAY_CLOSE_HOURS, close);
	}
	public String getFridayOpenHours() {
		return getString(ParseConstants.KEY_FRIDAY_OPEN_HOURS);
	}

	public void setFridayOpenHours(String open) {
		put(ParseConstants.KEY_FRIDAY_OPEN_HOURS, open);
	}
	
	public String getFridayCloseHours() {
		return getString(ParseConstants.KEY_FRIDAY_CLOSE_HOURS);
	}

	public void setFridayCloseHours(String close) {
		put(ParseConstants.KEY_FRIDAY_CLOSE_HOURS, close);
	}
	
	public String getSaturdayOpenHours() {
		return getString(ParseConstants.KEY_SATURDAY_OPEN_HOURS);
	}

	public void setSaturdayOpenHours(String open) {
		put(ParseConstants.KEY_SATURDAY_OPEN_HOURS, open);
	}
	
	public String getSaturdayCloseHours() {
		return getString(ParseConstants.KEY_SATURDAY_CLOSE_HOURS);
	}

	public void setSaturdayCloseHours(String close) {
		put(ParseConstants.KEY_SATURDAY_CLOSE_HOURS, close);
	}
	
	public String getNotes(){
		return getString(ParseConstants.KEY_NOTES);
	}
	
	public void setNotes(String notes) {
		put(ParseConstants.KEY_NOTES, notes);
	}
	
	public ParseFile getImage(){
		return getParseFile(ParseConstants.KEY_FILE);
	}
	
	public void setImage(ParseFile file){
		put(ParseConstants.KEY_FILE, file);
	}

}
