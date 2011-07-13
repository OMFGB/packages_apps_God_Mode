package com.t3hh4xx0r.addons.nightlies;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NightlyObject {
	
	private String mURL;
	private String mVersion;
	private String mBase;
	private String mDevice;
	private String mDate;
	private String mZipName;
	private String mInstallable;
	private String mDescription;
	
	LinearLayout mNightlyLayout;
	TextView mNightlyVersion;
	TextView mRomBaseVersion;
	TextView mCompiledDate;
	
	/**
	 * @param mDevice the mDevice to set
	 */
	public void setDevice(String mDevice) {
		this.mDevice = mDevice;
	}
	/**
	 * @return the mDevice
	 */
	public String getDevice() {
		return mDevice;
	}
	/**
	 * @param mBase the mBase to set
	 */
	public void setBase(String mBase) {
		this.mBase = mBase;
	}
	/**
	 * @return the mBase
	 */
	public String getBase() {
		return mBase;
	}
        /**
         * @param mDate the mDate to set
         */
        public void setDate(String mDate) {
                this.mDate = mDate;
        }
        /**
         * @return the mDate
         */
        public String getDate() {
                return mDate;
        }
	/**
	 * @param mVersion the mVersion to set
	 */
	public void setVersion(String mVersion) {
		this.mVersion = mVersion;
	}
	/**
	 * @return the mVersion
	 */
	public String getVersion() {
		return mVersion;
	}
	/**
	 * @param mURL the mURL to set
	 */
	public void setURL(String mURL) {
		this.mURL = mURL;
	}
	/**
	 * @return the mURL
	 */
	public String getURL() {
		return mURL;
	}
	/**
	 * @param mZipName the mZipName to set
	 */
	public void setZipName(String mZipName) {
		this.mZipName = mZipName;
	}
	/**
	 * @return the mZipName
	 */
	public String getZipName() {
		return mZipName;
	}
	/**
	 * @param mInstallable the mInstallable to set
	 */
	public void setInstallable(String mInstallable) {
		this.mInstallable = mInstallable;
	}
	/**
	 * @return the mInstallable
	 */
	public String getInstallable() {
		return mInstallable;
	}
	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}
	public String getDescription() {
		return mDescription;
	}
	
	
}
