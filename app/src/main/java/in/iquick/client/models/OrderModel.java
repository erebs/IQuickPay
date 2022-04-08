package in.iquick.client.models;

public class OrderModel {

    private String id;
    private String pstatus;
    private String total;
    private String discount;
    private String grand;
    private String placed;
    private String placed_desc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPstatus() {
        return pstatus;
    }

    public void setPstatus(String pstatus) {
        this.pstatus = pstatus;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getGrand() {
        return grand;
    }

    public void setGrand(String grand) {
        this.grand = grand;
    }

    public String getPlaced() {
        return placed;
    }

    public void setPlaced(String placed) {
        this.placed = placed;
    }

    public String getPlaced_desc() {
        return placed_desc;
    }

    public void setPlaced_desc(String placed_desc) {
        this.placed_desc = placed_desc;
    }


}
    