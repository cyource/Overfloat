package cyource.manasrawat.overfloat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Overfloat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overfloat);
        startService(new Intent(getApplicationContext(), FloatingButtonService.class));
        finish();
    }

    }
