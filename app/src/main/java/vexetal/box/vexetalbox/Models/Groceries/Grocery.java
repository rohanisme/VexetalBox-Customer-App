package vexetal.box.vexetalbox.Models.Groceries;

public class Grocery {

    public String Name,Desc,PushId,Image,Units,Category,CategoryName,Qty,Price,Price1,Price2,Status;
    public double Stock;

    public Grocery(){}

    public Grocery(String Name,String Desc,String PushId,String Image,String Units,String Category,String CategoryName,String Qty,String Price,String Price1,String Price2,String Status,double Stock){
        this.Name=Name;
        this.Desc=Desc;
        this.PushId=PushId;
        this.Image=Image;
        this.Units=Units;
        this.Category=Category;
        this.CategoryName=CategoryName;
        this.Qty=Qty;
        this.Price=Price;
        this.Price1=Price1;
        this.Price2=Price2;
        this.Status=Status;
        this.Stock=Stock;
    }


}

