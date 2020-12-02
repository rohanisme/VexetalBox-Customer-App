package vexetal.box.vexetalbox.Models.Coins;

public class Coins {
    public  String Date;
    public  String Generated;
    public  String PushId;
    public String TransactionName;
    public String TransactionType;
    public String UserId;
    public  String UserBalance;
    public String Amount;
    public String Status;

    public Coins(){

    }

    public Coins(String Date,String Generated,String PushId,String TransactionName,String TransactionType,String UserId,String UserBalance,String Amount,String Status){
        this.Date=Date;
        this.Generated=Generated;
        this.PushId=PushId;
        this.TransactionName=TransactionName;
        this.TransactionType=TransactionType;
        this.UserId=UserId;
        this.UserBalance=UserBalance;
        this.Amount=Amount;
        this.Status=Status;
    }
}
