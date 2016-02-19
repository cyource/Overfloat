package cyource.manasrawat.overfloat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class App extends AppCompatActivity {

    ArrayList<String> packageNames;
    ArrayList<String> appNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        final List<PackageInfo> appsList = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        Collections.sort(appsList, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo lhs, PackageInfo rhs) {
                return lhs.applicationInfo.loadLabel(getPackageManager()).toString().compareTo
                        (rhs.applicationInfo.loadLabel(getPackageManager()).toString());
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        packageNames = new ArrayList<>();
        appNames = new ArrayList<>();
        final Intent intent = new Intent(getApplicationContext(), Run.class);
        TypedValue typedValue = new TypedValue();
        getApplication().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);

        for (int i = 0; i < appsList.size(); i++) {
            PackageInfo packageInfo = appsList.get(i);
            final String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            final String packageName = packageInfo.packageName;
            final TextView textView = new TextView(this);
            textView.setText(appName);
            textView.setTextSize(18);
            textView.setClickable(true);
            textView.setBackgroundResource(typedValue.resourceId);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
            layoutParams.setMargins(40, 40, 40, 0);
            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setClickable(true);
            relativeLayout.setBackgroundResource(typedValue.resourceId);
            RelativeLayout.LayoutParams relativeLayoutLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
            relativeLayoutLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            if (getPackageManager().getLaunchIntentForPackage(packageName) != null) {
                relativeLayout.addView(textView);
                final CheckBox checkBox = new CheckBox(this);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (checkBox.isChecked()) {
                            packageNames.add(packageName);
                            appNames.add(appName);
                        } else {
                            packageNames.remove(packageName);
                            appNames.remove(appName);
                        }
                    }
                });
                onClick(relativeLayout, checkBox);
                onClick(textView, checkBox);
                relativeLayout.addView(checkBox, relativeLayoutLayoutParams);
                linearLayout.addView(relativeLayout, layoutParams);
            }
        }
        View view = new View(this);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40);
        linearLayout.addView(view, layoutParams2);

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
                startService(intent
                        .putStringArrayListExtra("PACKAGE_NAMES", packageNames)
                        .putStringArrayListExtra("APP_NAMES", appNames));
            }
        });
    }

    public void onClick(View view, final CheckBox checkBox) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
            }
        });
    }

}