package com.xgames.testgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    Settings settings;

    Switch ver,hor,cross;
    RadioButton easy,med,hard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new Settings(this);

        initializeElements();
    }

    private void initializeElements(){
        ver = findViewById(R.id.sw_ver);
        hor = findViewById(R.id.sw_hor);
        cross = findViewById(R.id.cross);

        easy = findViewById(R.id.easy);
        med = findViewById(R.id.medium);
        hard = findViewById(R.id.hard);

        findViewById(R.id.applyButton).setOnClickListener(this);
        findViewById(R.id.restoreButton).setOnClickListener(this);

        setDefaultSettings();
    }

    private void setDefaultSettings(){
        ver.setChecked(settings.getVertical());
        hor.setChecked(settings.getHorizontal());
        cross.setChecked(settings.getCross());
        easy.setChecked(settings.getEasy());
        med.setChecked(settings.getMedium());
        hard.setChecked(settings.getHard());
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        if(i == R.id.applyButton){
            applyChanges();
        }
        else if(i == R.id.restoreButton){
            restoreSettings();
        }
    }

    private Boolean checkDirections(){
        if(!ver.isChecked() && !hor.isChecked() && !cross.isChecked()){
            Toast.makeText(this,"Yön seçeneklerinden en az birini işaretleyiniz!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void applyChanges(){
        if(!checkDirections())
            return;

        settings.setVertical(ver.isChecked());
        settings.setHorizontal(hor.isChecked());
        settings.setCross(cross.isChecked());
        settings.setEasy(easy.isChecked());
        settings.setMedium(med.isChecked());
        settings.setHard(hard.isChecked());
        settings.setApplyChanges(true);
        settings.updateData();

        Toast.makeText(this,"Değişiklikler kaydedildi.",Toast.LENGTH_SHORT).show();
    }

    private void restoreSettings(){
        Toast.makeText(this,"Ayarlar sıfırlandı.",Toast.LENGTH_SHORT).show();
        settings.setDefaultData();
        setDefaultSettings();
    }
}
