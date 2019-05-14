package com.xgames.testgame;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    private Boolean horizontal,vertical,cross;
    private Boolean easy,medium,hard;
    private Boolean applyChanges;

    SharedPreferences pref;

    public Settings(Context context){
        pref = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        loadData();
    }

    public void setDefaultData(){
        SharedPreferences.Editor ed = pref.edit();

        setHorizontal(true);
        setVertical(true);
        setCross(true);
        setEasy(false);
        setMedium(false);
        setHard(true);

        ed.putBoolean("Horizontal",true);
        ed.putBoolean("Vertical",true);
        ed.putBoolean("Cross",true);
        ed.putBoolean("Easy",false);
        ed.putBoolean("Medium",false);
        ed.putBoolean("Hard",true);
        ed.putBoolean("Apply",false);

        ed.commit();
    }

    public void loadData(){
        horizontal = pref.getBoolean("Horizontal",false);
        vertical = pref.getBoolean("Vertical",false);
        cross = pref.getBoolean("Cross",false);
        easy = pref.getBoolean("Easy",false);
        medium = pref.getBoolean("Medium",false);
        hard = pref.getBoolean("Hard",false);
        applyChanges = pref.getBoolean("Apply",false);
    }

    public void updateData(){
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("Horizontal",horizontal);
        ed.putBoolean("Vertical",vertical);
        ed.putBoolean("Cross",cross);
        ed.putBoolean("Easy",easy);
        ed.putBoolean("Medium",medium);
        ed.putBoolean("Hard",hard);
        ed.putBoolean("Apply",true);

        ed.apply();
    }

    public Boolean getApplyChanges() { return applyChanges; }

    public Boolean getHorizontal() { return horizontal; }

    public Boolean getVertical() { return vertical; }

    public Boolean getCross() { return cross; }

    public Boolean getEasy() { return easy; }

    public Boolean getMedium() { return medium; }

    public Boolean getHard() { return hard; }

    public void setApplyChanges(Boolean applyChanges) { this.applyChanges = applyChanges; }

    public void setCross(Boolean cross) { this.cross = cross; }

    public void setEasy(Boolean easy) { this.easy = easy; }

    public void setHard(Boolean hard) { this.hard = hard; }

    public void setMedium(Boolean medium) { this.medium = medium; }

    public void setHorizontal(Boolean horizontal) { this.horizontal = horizontal; }

    public void setVertical(Boolean vertical) { this.vertical = vertical; }
}
