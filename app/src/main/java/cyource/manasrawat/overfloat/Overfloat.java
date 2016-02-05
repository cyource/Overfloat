package cyource.manasrawat.overfloat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Overfloat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overfloat);
        List<PackageInfo> appsList = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        for (int i = 0; i < appsList.size(); i++) {
            PackageInfo packageInfo = appsList.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            final String packageName = packageInfo.packageName;
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
            final TextView textView = new TextView(this);
            textView.setText(appName);
            textView.setTextSize(18);
            textView.setClickable(true);
            TypedValue typedValue = new TypedValue();
            getApplication().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            textView.setBackgroundResource(typedValue.resourceId);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
            int bottom = 0;
            if (i == appsList.size() - 1) {
                bottom = 40;
            }
            layoutParams.setMargins(40, 40, 40, bottom);
            if (getPackageManager().getLaunchIntentForPackage(packageName) != null) {
                linearLayout.addView(textView, layoutParams);
            }
            final Intent intent = new Intent(getApplicationContext(), FloatingButtonService.class);
            final ImageView imageView = (ImageView) findViewById(R.id.imageView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startService(intent.
                            putExtra("PACKAGE_NAME", packageName));
                    imageView.setVisibility(View.VISIBLE);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopService(intent);
                    imageView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}
