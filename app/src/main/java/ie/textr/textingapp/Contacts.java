package ie.textr.textingapp;

public class Contacts {

    int num;
    String name;
    String mobile_no;

    public Contacts(int num, String name, String mobile_no) {
        this.num = num;
        this.name = name;
        this.mobile_no = mobile_no;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
}
