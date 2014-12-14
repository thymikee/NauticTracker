package nautictracker;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TRACK_POINT.
 */
public class TrackPoint {

    private Long trip_id;
    private String latitude;
    private String longitude;
    private java.util.Date timestamp;
    private Long sequence;

    public TrackPoint() {
    }

    public TrackPoint(Long trip_id) {
        this.trip_id = trip_id;
    }

    public TrackPoint(Long trip_id, String latitude, String longitude, java.util.Date timestamp, Long sequence) {
        this.trip_id = trip_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.sequence = sequence;
    }

    public Long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(Long trip_id) {
        this.trip_id = trip_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.util.Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

}