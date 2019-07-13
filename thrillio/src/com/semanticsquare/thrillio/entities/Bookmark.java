package com.semanticsquare.thrillio.entities;

import com.semanticsquare.thrillio.constants.KidFriendlyStatus;
import com.semanticsquare.thrillio.partner.Shareable;

public abstract class Bookmark implements Shareable {
	private long id;
	private String title;
	private String profileUrl;
	private String kidFriendlyStatus = KidFriendlyStatus.UNKNOWN;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getKidFriendlyStatus() {
		return kidFriendlyStatus;
	}

	public void setKidFriendlyStatus(String kidFriendlyStatus) {
		this.kidFriendlyStatus = kidFriendlyStatus;
	}

	public abstract boolean isKidFriendlyEligible();
}
