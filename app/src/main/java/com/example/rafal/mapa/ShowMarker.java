package com.example.rafal.mapa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class ShowMarker extends AppCompatActivity {
    int indexOfMarker;
    String population ;
    String title;
    String position;
    String positionStart = position;
    double positionX;
    double positionY;

    EditText populationET;
    EditText titleET;
    EditText positionET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_marker);
        populationET = (EditText)findViewById(R.id.population);
        titleET = (EditText)findViewById(R.id.title);
        positionET = (EditText)findViewById(R.id.position);
        indexOfMarker = getIntent().getIntExtra("index",-1);
        population = getIntent().getStringExtra("population");
        title = getIntent().getStringExtra("title");
        position = getIntent().getStringExtra("position");
        positionStart = position;
        populationET.setText(population);
        titleET.setText(title);
        positionET.setText(position);

        ImageButton backkButton = (ImageButton)findViewById(R.id.backButton);
        backkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(positionX+" "+positionY);
                assignValue();
                System.out.println(indexOfMarker);
                System.out.println(positionX+" "+positionY);
//lat/lng: (51.11,17.03)
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                intent.putExtra("population",population);
                intent.putExtra("title",title);
                intent.putExtra("positionX",positionX);
                intent.putExtra("positionY",positionY);
                intent.putExtra("index",indexOfMarker);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

    }

    private void assignValue(){
        position = positionET.getText().toString();
        try {
            positionX = Double.parseDouble(position.substring(position.indexOf("(")+1, position.indexOf(",")));
            positionY = Double.parseDouble(position.substring(position.indexOf(",") + 1, position.indexOf(")")));
        }catch(Exception ex){
            positionX = Double.parseDouble(positionStart.substring(positionStart.indexOf("(")+1, positionStart.indexOf(",")));
            positionY = Double.parseDouble(positionStart.substring(positionStart.indexOf(",") + 1, positionStart.indexOf(")")));
        }

        title=titleET.getText().toString();
        population=populationET.getText().toString();
    }

}
