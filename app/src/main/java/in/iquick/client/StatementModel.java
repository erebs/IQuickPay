package in.iquick.client;

public class StatementModel {

    private String Amount;
    private String Date;
    private String Type;
    private String Dis;
    private String IdRecharge;

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getIdRecharge() {
        return IdRecharge;
    }

    public void setIdRecharge(String IdRecharge) {
        this.IdRecharge = IdRecharge;
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getDis() {
        return Dis;
    }

    public void setDis(String Dis) {
        this.Dis = Dis;
    }


}