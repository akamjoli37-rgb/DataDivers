package CrimeAnaylzer;

public class CrimeRecord {
    public String incidentNumber;
    public String offenseCode;
    public String offenseCodeGroup;
    public String offenseDescription;
    public String district;
    public String status;
    public String reportingArea;
    public String shooting;
    public String occurredOnDate;
    public String ucrPart;
    public String street;
    public String location;

    public CrimeRecord(){

    }

    public CrimeRecord(String incidentNumber, String offenseCode, String offenseCodeGroup, String offenseDescription, String district, String status, String reportingArea, String  shooting, String occurredOnDate, String ucrPart, String street, String location){
        this.incidentNumber = incidentNumber;
        this.offenseCode = offenseCode;
        this.offenseCodeGroup = offenseCodeGroup;
        this.offenseDescription = offenseDescription;
        this.district = district;
        this.status = status;
        this.reportingArea = reportingArea;
        this.shooting = shooting;
        this.occurredOnDate = occurredOnDate;
        this.ucrPart = ucrPart;
        this.street = street;
        this.location = location;
    }

    public String getIncidentNumber() {
        return incidentNumber;
    }

    public void setIncidentNumber(String incidentNumber) {
        this.incidentNumber = incidentNumber;
    }

    public String getOffenseCode() {
        return offenseCode;
    }

    public void setOffenseCode(String offenseCode) {
        this.offenseCode = offenseCode;
    }

    public String getOffenseCodeGroup() {
        return offenseCodeGroup;
    }

    public void setOffenseCodeGroup(String offenseCodeGroup) {
        this.offenseCodeGroup = offenseCodeGroup;
    }

    public String getOffenseDescription() {
        return offenseDescription;
    }

    public void setOffenseDescription(String offenseDescription) {
        this.offenseDescription = offenseDescription;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportingArea() {
        return reportingArea;
    }

    public void setReportingArea(String reportingArea) {
        this.reportingArea = reportingArea;
    }

    public String getShooting() {
        return shooting;
    }

    public void setShooting(String shooting) {
        this.shooting = shooting;
    }

    public String getOccurredOnDate() {
        return occurredOnDate;
    }

    public void setOccurredOnDate(String occurredOnDate) {
        this.occurredOnDate = occurredOnDate;
    }

    public String getUcrPart() {
        return ucrPart;
    }

    public void setUcrPart(String ucrPart) {
        this.ucrPart = ucrPart;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

