package ast.adrs.farmer.HomeAuxiliares;

public class DModel_Message {
    String desc;
    String date;
    String time;
    String reportId;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public DModel_Message(String date, String time, String desc, String reportId) {
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.reportId = reportId;
    }

    public DModel_Message(String date, String time, String desc) {
        this.desc = desc;
        this.date = date;
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
