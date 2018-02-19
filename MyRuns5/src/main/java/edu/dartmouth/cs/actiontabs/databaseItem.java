package edu.dartmouth.cs.actiontabs;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class databaseItem {
    private String ID = "";                //ID of the item
    private String Date = "";              //Date of the item
    private String Time = "";              //time of the item
    private double Duration = -1;          //the total time for the activity
    private double Distance = -1;          //the total distance for the activity
    private int Calories = -1;             //the total calories cost in the activity
    private int HeartRate = -1;            //the heart rate in the activity
    private String Comment = "";           //comments for the activity
    private String InputType = "";         //input type
    private String ActivityType = "";      //the activity type

    private List<LatLng> Latlngs = new ArrayList<>();    //record of all positions
    private double Climb = -1;                           //Climb
    private double AvgSpeed = -1;                        //Average Speed
    private double CurSpeed = -1;                        //Current Speed

    public databaseItem(String ID, String Date, String Time, double Duration, double Distance, int Calories,
                        int HeartRate, String Comment, String InputType, String ActivityType, double Climb,
                        double AvgSpeed, double CurSpeed, List<LatLng> Latlngs){
        this.ID = ID;
        this.Date = Date;
        this.Time = Time;
        this.Duration = Duration;
        this.Distance = Distance;
        this.Calories = Calories;
        this.HeartRate = HeartRate;
        this.Comment = Comment;
        this.InputType = InputType;
        this.ActivityType = ActivityType;
        this.Climb = Climb;
        this.AvgSpeed = AvgSpeed;
        this.CurSpeed = CurSpeed;
        this.Latlngs = Latlngs;
    }

    public databaseItem(){}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public double getDuration() {
        return Duration;
    }

    public void setDuration(double duration) {
        Duration = duration;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public int getCalories() {
        return Calories;
    }

    public void setCalories(int calories) {
        Calories = calories;
    }

    public int getHeartRate() {
        return HeartRate;
    }

    public void setHeartRate(int heartRate) {
        HeartRate = heartRate;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getInputType() {
        return InputType;
    }

    public void setInputType(String inputType) {
        InputType = inputType;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public List<LatLng> getLatlngs() {
        return Latlngs;
    }

    public void setLatlngs(List<LatLng> latlngs) {
        Latlngs = latlngs;
    }

    public double getClimb() {
        return Climb;
    }

    public void setClimb(double climb) {
        Climb = climb;
    }

    public double getAvgSpeed() {
        return AvgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        AvgSpeed = avgSpeed;
    }

    public double getCurSpeed() {
        return CurSpeed;
    }

    public void setCurSpeed(double curSpeed) {
        CurSpeed = curSpeed;
    }
}
