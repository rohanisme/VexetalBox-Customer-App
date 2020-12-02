package vexetal.box.vexetalbox.Models.Address;

public class Address {
    public String Address;
    public String Coord;
    public String Flat;
    public String Name;
    public String PushId;

    public Address(){}

    public Address(String Address,String Coord,String Flat,String Name,String PushId){
        this.Address=Address;
        this.Coord=Coord;
        this.Flat=Flat;
        this.Name=Name;
        this.PushId=PushId;
    }
}
