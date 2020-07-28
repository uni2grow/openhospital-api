package org.isf.disease.dto;

import javax.validation.constraints.NotNull;

import org.isf.distype.dto.DiseaseTypeDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Class representing a disease")
public class DiseaseDTO {
	@NotNull
	private String code;

	@NotNull
	@ApiModelProperty(notes = "Disease description")
    private String description;

	@NotNull
	@ApiModelProperty(notes = "Disease type")
	private DiseaseTypeDTO diseaseType; 

	@NotNull
	@ApiModelProperty(notes = "indicates whether the disease is an OPD disease", example="true")
	private boolean opdInclude;

	@NotNull
	@ApiModelProperty(notes = "indicates whether the disease is an IPD-IN disease", example="true")
	private boolean ipdInInclude;

	@NotNull
	@ApiModelProperty(notes = "indicates whether the disease is an IPD-OUT disease", example="true")
	private boolean ipdOutInclude;
	
	private int hashCode = 0;
	
	private Integer lock;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DiseaseTypeDTO getDiseaseType() {
		return diseaseType;
	}

	public void setDiseaseType(DiseaseTypeDTO diseaseType) {
		this.diseaseType = diseaseType;
	}

	public boolean isOpdInclude() {
		return opdInclude;
	}

	public void setOpdInclude(boolean opdInclude) {
		this.opdInclude = opdInclude;
	}

	public boolean isIpdInInclude() {
		return ipdInInclude;
	}

	public void setIpdInInclude(boolean ipdInInclude) {
		this.ipdInInclude = ipdInInclude;
	}

	public boolean isIpdOutInclude() {
		return ipdOutInclude;
	}

	public void setIpdOutInclude(boolean ipdOutInclude) {
		this.ipdOutInclude = ipdOutInclude;
	}
	
	@ApiModelProperty(hidden= true)
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	@ApiModelProperty(hidden= true)
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

}
