package com.ibm.au.optim.suro.model.entities.domain.learning;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class SurgeryCluster {

	private String clusterId;
	private String clusterName;
	private String departmentId;
	private float duration;
	private float changeOverTime;
	private float lengthOfStay;
	private float icuProbability;
	private String wardId;
	private String specialistTypeId;
	private String urgencyCategoryId;
	private float wies;

	public SurgeryCluster() { }

	public SurgeryCluster(String clusterId,
												String clusterName,
												String departmentId,
												float duration,
												float changeOverTime,
												float lengthOfStay,
												float icuProbability,
												String wardId,
												String specialistTypeId,
												String urgencyCategoryId,
												float wies) {
		this.clusterId = clusterId;
		this.clusterName = clusterName;
		this.departmentId = departmentId;
		this.duration = duration;
		this.changeOverTime = changeOverTime;
		this.lengthOfStay = lengthOfStay;
		this.icuProbability = icuProbability;
		this.wardId = wardId;
		this.specialistTypeId = specialistTypeId;
		this.urgencyCategoryId = urgencyCategoryId;
		this.wies = wies;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getChangeOverTime() {
		return changeOverTime;
	}

	public void setChangeOverTime(float changeOverTime) {
		this.changeOverTime = changeOverTime;
	}

	public float getLengthOfStay() {
		return lengthOfStay;
	}

	public void setLengthOfStay(float lengthOfStay) {
		this.lengthOfStay = lengthOfStay;
	}

	public float getIcuProbability() {
		return icuProbability;
	}

	public void setIcuProbability(float icuProbability) {
		this.icuProbability = icuProbability;
	}

	public String getWardId() {
		return wardId;
	}

	public void setWardId(String wardId) {
		this.wardId = wardId;
	}

	public String getSpecialistTypeId() {
		return specialistTypeId;
	}

	public void setSpecialistTypeId(String specialistTypeId) {
		this.specialistTypeId = specialistTypeId;
	}

	public String getUrgencyCategoryId() {
		return urgencyCategoryId;
	}

	public void setUrgencyCategoryId(String urgencyCategoryId) {
		this.urgencyCategoryId = urgencyCategoryId;
	}

	public float getWies() {
		return wies;
	}

	public void setWies(float wies) {
		this.wies = wies;
	}
}
