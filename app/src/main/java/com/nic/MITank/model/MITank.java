package com.nic.MITank.model;

import android.graphics.Bitmap;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class MITank {

    private String distictCode;
    private String districtName;
    private String blockCode;
    private String Longitude;
    private String typeOfPhoto;
    private String imageRemark;
    private String dateTime;
    private String imageAvailable;
    private String HabCode;
    private String Description;
    private String Latitude;
    private String BeneficiaryName;
    private String HabitationName;
    private String pmayId;
    private String fatherName;
    private String SeccId;
    private String PvCode;
    private String PvName;
    private String blockName;

    private String miTankStructureId;
    private String miTankStructureName;
    private String miTankSurveyId;
    private String minorIrrigationType;
    private String nameOftheMITank;
    private String localName;
    private String area;
    private String miTankStructureDetailId;
    private String miTankStructureSerialId;
    private String miTankConditionId;
    private String miTankConditionName;
    private String miTankSkillLevel;
    public String pointType;
    public Integer pointSerialNo;

    public String getMiTankStructureId() {
        return miTankStructureId;
    }

    public void setMiTankStructureId(String miTankStructureId) {
        this.miTankStructureId = miTankStructureId;
    }

    public String getMiTankStructureName() {
        return miTankStructureName;
    }

    public void setMiTankStructureName(String miTankStructureName) {
        this.miTankStructureName = miTankStructureName;
    }

    public String getMiTankSurveyId() {
        return miTankSurveyId;
    }

    public void setMiTankSurveyId(String miTankSurveyId) {
        this.miTankSurveyId = miTankSurveyId;
    }

    public String getMinorIrrigationType() {
        return minorIrrigationType;
    }

    public void setMinorIrrigationType(String minorIrrigationType) {
        this.minorIrrigationType = minorIrrigationType;
    }

    public String getNameOftheMITank() {
        return nameOftheMITank;
    }

    public void setNameOftheMITank(String nameOftheMITank) {
        this.nameOftheMITank = nameOftheMITank;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMiTankStructureDetailId() {
        return miTankStructureDetailId;
    }

    public void setMiTankStructureDetailId(String miTankStructureDetailId) {
        this.miTankStructureDetailId = miTankStructureDetailId;
    }

    public String getMiTankStructureSerialId() {
        return miTankStructureSerialId;
    }

    public void setMiTankStructureSerialId(String miTankStructureSerialId) {
        this.miTankStructureSerialId = miTankStructureSerialId;
    }

    public String getMiTankConditionId() {
        return miTankConditionId;
    }

    public void setMiTankConditionId(String miTankConditionId) {
        this.miTankConditionId = miTankConditionId;
    }

    public String getMiTankConditionName() {
        return miTankConditionName;
    }

    public void setMiTankConditionName(String miTankConditionName) {
        this.miTankConditionName = miTankConditionName;
    }

    public String getMiTankSkillLevel() {
        return miTankSkillLevel;
    }

    public void setMiTankSkillLevel(String miTankSkillLevel) {
        this.miTankSkillLevel = miTankSkillLevel;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getPmayId() {
        return pmayId;
    }

    public void setPmayId(String pmayId) {
        this.pmayId = pmayId;
    }


    public String getBeneficiaryName() {
        return BeneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        BeneficiaryName = beneficiaryName;
    }

    public String getHabitationName() {
        return HabitationName;
    }

    public void setHabitationName(String habitationName) {
        HabitationName = habitationName;
    }

    public String getSeccId() {
        return SeccId;
    }

    public void setSeccId(String seccId) {
        SeccId = seccId;
    }

    public String getHabCode() {
        return HabCode;
    }

    public void setHabCode(String habCode) {
        HabCode = habCode;
    }

    public String getTypeOfPhoto() {
        return typeOfPhoto;
    }

    public void setTypeOfPhoto(String typeOfPhoto) {
        this.typeOfPhoto = typeOfPhoto;
    }

    public String getImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(String imageAvailable) {
        this.imageAvailable = imageAvailable;
    }


    public String getImageRemark() {
        return imageRemark;
    }

    public void setImageRemark(String imageRemark) {
        this.imageRemark = imageRemark;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }


    public String getPvName() {
        return PvName;
    }

    public void setPvName(String name) {
        PvName = name;
    }


    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    private Bitmap Image;

    public String getPvCode() {
        return PvCode;
    }

    public void setPvCode(String pvCode) {
        this.PvCode = pvCode;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public Integer getPointSerialNo() {
        return pointSerialNo;
    }

    public void setPointSerialNo(Integer pointType) {
        this.pointSerialNo = pointType;
    }
}