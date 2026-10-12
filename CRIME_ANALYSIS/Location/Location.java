package Location;

public class Location {


     String lat;
     String lng;


     public Location(String lat, String lng){
        this.lat = lat;
        this.lng = lng;
     }



    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
