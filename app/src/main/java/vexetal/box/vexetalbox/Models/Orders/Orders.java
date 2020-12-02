package vexetal.box.vexetalbox.Models.Orders;

public class Orders {
    public String Address;
    public String Amount;
    public String CName;
    public String DeliveryCharges;
    public String Flat;
    public String LocationCoordinates;
    public String Number;
    public String OrderDate;
    public String OrderDateTime;
    public String OrderNo;
    public String OrderType;
    public String Payment;
    public String Pushid;
    public String Qty;
    public String RazorpayId;
    public String Status;
    public String Total;
    public String UserId;
    public String ItemsDetails;

    public  Orders(){}

    public Orders(String Address,String Amount,String CName,String DeliveryCharges,String Flat,String LocationCoordinates,String Number,String OrderDate,
                  String OrderDateTime,String OrderNo,String OrderType,String Payment,String Pushid,String Qty,String RazorpayId,String Status,String Total,String UserId,String ItemsDetails){
        this.Address=Address;
        this.Amount=Amount;
        this.CName=CName;
        this.DeliveryCharges=DeliveryCharges;
        this.Flat=Flat;
        this.LocationCoordinates=LocationCoordinates;
        this.Number=Number;
        this.OrderDate=OrderDate;
        this.OrderDateTime=OrderDateTime;
        this.OrderNo=OrderNo;
        this.OrderType=OrderType;
        this.Payment=Payment;
        this.Pushid=Pushid;
        this.Qty=Qty;
        this.RazorpayId=RazorpayId;
        this.Status=Status;
        this.Total=Total;
        this.UserId=UserId;
        this.ItemsDetails=ItemsDetails;
    }
}
