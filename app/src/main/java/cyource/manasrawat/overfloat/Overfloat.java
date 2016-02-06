package cyource.manasrawat.overfloat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Overfloat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overfloat);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));
        List<PackageInfo> appsList = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        Collections.sort(appsList, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo lhs, PackageInfo rhs) {
                return lhs.applicationInfo.loadLabel(getPackageManager()).toString().compareTo
                        (rhs.applicationInfo.loadLabel(getPackageManager()).toString());
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        for (int i = 0; i < appsList.size(); i++) {
            PackageInfo packageInfo = appsList.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            final String packageName = packageInfo.packageName;
            final TextView textView = new TextView(this);
            textView.setText(appName);
            textView.setTextSize(18);
            textView.setClickable(true);
            TypedValue typedValue = new TypedValue();
            getApplication().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            textView.setBackgroundResource(typedValue.resourceId);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
            layoutParams.setMargins(40, 40, 40, 0);
            if (getPackageManager().getLaunchIntentForPackage(packageName) != null)
                linearLayout.addView(textView, layoutParams);
            final Intent intent = new Intent(getApplicationContext(), FloatingButtonService.class);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopService(intent);
                    startService(intent.
                            putExtra("PACKAGE_NAME", packageName));
                }
            });
        }
        View view = new View(this);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40);
        linearLayout.addView(view, layoutParams2);
    }
}
