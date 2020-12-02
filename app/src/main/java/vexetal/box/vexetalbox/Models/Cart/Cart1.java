package vexetal.box.vexetalbox.Models.Cart;

public class Cart1 {
    public String Name;
    public String Price;
    public String PushId;
    public String Qty;
    public String Total;
    public String Units;
    public String Image;
    public String Min;


    public Cart1(){}

    public Cart1(String Name,String Price,String PushId,String Qty,String Total,String Units,String Image,String Min){
        this.Name=Name;
        this.Price=Price;
        this.PushId=PushId;
        this.Qty=Qty;
        this.Total=Total;
        this.Units=Units;
        this.Image=Image;
        this.Min=Min;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPushId() {
        return PushId;
    }

    public void setPushId(String pushId) {
        PushId = pushId;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getUnits() {
        return Units;
    }

    public void setUnits(String units) {
        Units = units;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


}
